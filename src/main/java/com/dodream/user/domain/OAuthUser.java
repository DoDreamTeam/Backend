package com.dodream.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthUser {
    String providerId;
    String profileImage;
    String username;
    AuthEnum provider;
    boolean isJoined;
}
