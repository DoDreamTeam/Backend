package com.dodream.user.service;

import com.dodream.user.domain.AuthEnum;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import com.dodream.util.OAuth2Properties;
import com.dodream.util.TokenUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OAuth2Properties oAuth2Properties;
    private final TokenUtils tokenUtils;

    @Override
    public User oAuthUser(String code, AuthEnum provider) {
        // 1. code를 통해 provider에서 제공하는 accessToken 가져온다.
        String providedAccessToken = getAccessToken(code, provider);

        // 2. provider에서 제공하는 accessToken으로 oAuthUserInfo를 추출한다.
        JsonNode oAuthUserNode = generateOAuthUserNode(providedAccessToken, provider);
        return getOAuthUserInfo(oAuthUserNode, provider);
    }

    @Override
    public String login(User user, HttpServletResponse res) {
        Map<String, String> tokenMap = tokenUtils.generateToken(user);

        // DB에 기록(refresh)
        user.setRefreshToken(tokenMap.get("refreshToken"));
        userRepository.save(user);
        // HEADER에 추가(refresh)
        tokenUtils.setRefreshTokenCookie(res, tokenMap.get("refreshToken"));
        // BODY에 추가(access)
        return tokenMap.get("accessToken");
    }

    public String getAccessToken(String code, AuthEnum provider) {
        // 설정 가져오기
        OAuth2Properties.Client client = oAuth2Properties.getClients().get(provider.getAuth());

        log.info(client.getClientId());
        log.info(client.getClientSecret());
        log.info(client.getRedirectUri());

        // 1. code를 통해 google에서 제공하는 accessToken 가져온다.
        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(client.getClientId(), client.getClientSecret());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("client_id", client.getClientId());
        params.add("client_secret", client.getClientSecret());
        params.add("code", decodedCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", client.getRedirectUri());

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> responseEntity = rt.postForEntity(client.getTokenUri(), requestEntity, Map.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 가져올 수 없음");
        }

        return (String) responseEntity.getBody().get("access_token");
    }

    private JsonNode generateOAuthUserNode(String accessToken, AuthEnum provider) {
        // 설정 가져오기
        OAuth2Properties.Client client = oAuth2Properties.getClients().get(provider.getAuth());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = rt.exchange(client.getUserInfoRequestUri(), HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 가져올 수 없음");
        }

        return responseEntity.getBody();
    }

    private User getOAuthUserInfo(JsonNode oAuthUserNode, AuthEnum provider) {
        String providerId = null;
        String username = null;
        String profileImage = null;

        System.out.println(oAuthUserNode);

        if (provider.equals(AuthEnum.NAVER)) {
            providerId = oAuthUserNode.get("response").get("id").asText();
            username = oAuthUserNode.get("response").get("name").asText();
            profileImage = oAuthUserNode.get("response").get("profile_image").asText();
        } else if (provider.equals(AuthEnum.KAKAO)) {
            providerId = oAuthUserNode.get("id").asText();
            username = oAuthUserNode.get("properties").get("nickname").asText();
            profileImage = oAuthUserNode.get("properties").get("profile_image").asText();
        } else if (provider.equals(AuthEnum.GOOGLE)) {
            providerId = oAuthUserNode.get("sub").asText();
            username = oAuthUserNode.get("name").asText();
            profileImage = oAuthUserNode.get("picture").asText();
        }

        return User.builder()
            .providerId(providerId)
            .provider(provider.getAuth())
            .profileImage(profileImage)
            .username(username)
            .build();
    }
}
