package com.dodream.mypage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;
    private final BookmarkRepository bookmarkRepository;

    private final AmazonS3 amazonS3;
    private static String profileName;
    private static String uuidString;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.toProfileDTO(user);
    }

    // 사용자의 문제집 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getUserBooks(Long userId, Pageable pageable) {
        // 사용자의 문제집 리스트
        Page<UserBook> userBooksPage = userBookRepository
                .findByUserIdAndBookSecretFalseOrderByBookCreatedAtDesc(userId, pageable);

        // 모든 BookResponse 생성 (중복 제거 로직 제거)
        List<BookResponse> bookResponses = userBooksPage.getContent().stream()
                .map(userBook -> {
                    Book book = userBook.getBook();
                    return BookResponse.builder()
                            .id(book.getId())
                            .title(book.getTitle())
                            .username(book.getUser() != null ? book.getUser().getUsername() : null)
                            .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book))
                            .category(book.getCategory().name())
                            .createdAt(book.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        // 중복 제거 없이 반환
        return new PageImpl<>(bookResponses, pageable, userBooksPage.getTotalElements());
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
            .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return uploadFile(uploadFile, dirName);
    }

    private String uploadFile(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        uuidString = fileName.split("/")[1].substring(0, 36);   // uuid 길이는 고정
        profileName = uploadFile.getName();                             // 한글 파일 url로 불러올 시 변경되는 이슈 처리
        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제가 완료되었습니다.");
        } else {
            log.info("파일 삭제가 실패되었습니다.");
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.error("MultipartFile is null or empty");
            return Optional.empty();
        }

        File convertFile = new File(System.getProperty("java.io.tmpdir") +
            System.getProperty("file.separator") + file.getOriginalFilename());

        if (!convertFile.createNewFile()) {
            log.error("Failed to create new file for conversion: " + convertFile.getAbsolutePath());
            return Optional.empty();
        }

        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
            return Optional.of(convertFile);
        } catch (IOException e) {
            log.error("Error writing bytes to file: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    // 사용자 프로필 수정
    @Override
    @Transactional
    public UserInfoResponse updateUserProfile(String newUserName, MultipartFile file)
        throws IOException {
        User loginuser = (User) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        Long loginUserId = loginuser.getId();

        User user = userRepository.findById(loginUserId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지, 기존 유저명
        String existingProfileImage = user.getProfileImage();
        String currentName = user.getUsername();

        if (!file.isEmpty() && !newUserName.isEmpty()) {                        // 업로드 파일 + 새로운 유저명
            updateUserNameAndUserProfileImage(newUserName, file, existingProfileImage, user);
        } else if (file.isEmpty() && !user.getUsername().isEmpty()) {            // 빈 파일 + 새로운 유저명
            user.updateProfile(newUserName, existingProfileImage);
        } else if (!file.isEmpty() && newUserName.isEmpty()) {                   // 업로드 파일 + 기존 유저명
            updateUserNameAndUserProfileImage(currentName, file, existingProfileImage, user);
        }

        return UserInfoResponse.toProfileDTO(user);
    }

    private void updateUserNameAndUserProfileImage(String newUserName, MultipartFile file,
        String existingProfileImage, User user) throws IOException {
        if (!existingProfileImage.isEmpty()) {
            String deleteFilename = existingProfileImage.split("/")[3]
                + "/" + existingProfileImage.split("/")[4];
            deleteImageFromS3(deleteFilename);
        }
        String newProfileImage = getUploadFile(file);
        user.updateProfile(newUserName, newProfileImage);
    }

    private String getUploadFile(MultipartFile file) throws IOException {
        String uploadImage = upload(file, "profile-image");
        return uploadImage.split("/")[0] + "//"
            + uploadImage.split("/")[2] + "/"
            + uploadImage.split("/")[3] + "/"
            + uuidString
            + profileName;
    }

    private void deleteImageFromS3(String fileName) {
        try {
            amazonS3.deleteObject(bucket, fileName);
            log.info("파일 삭제가 완료되었습니다: {}", fileName);
        } catch (Exception e) {
            log.error("파일 삭제에 실패했습니다: {}", fileName, e);
            throw new BaseException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

}