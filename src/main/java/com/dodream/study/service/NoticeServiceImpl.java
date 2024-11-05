package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeRequest;
import com.dodream.study.domain.NoticeResponse;
import com.dodream.study.domain.UpdateNoticeRequest;
import com.dodream.study.domain.UpdateNoticeResponse;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.NoticeRepository;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Override
    @Transactional
    public NoticeResponse createNotice(Long studyId, NoticeRequest noticeRequest, User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        Optional<Notice> existingNotice =
            noticeRepository.findDeletedEmptyContentNoticeByStudyId(studyId);

        // 공지사항이 이미 존재한다면 (soft delete)
        if (existingNotice.isPresent()) {
            return updateExistingNotice(existingNotice.get(), noticeRequest);
        }

        checkUserRole(studyId, user);

        Notice notice = noticeRequest.toEntity(study);
        Notice savedNotice = noticeRepository.save(notice);

        return buildNoticeResponse(savedNotice, study.getId());
    }

    private NoticeResponse updateExistingNotice(Notice noticeToUpdate, NoticeRequest noticeRequest) {
        noticeToUpdate.setDeleted(false);
        noticeToUpdate.updateNotice(noticeRequest.getContent());

        return buildNoticeResponse(noticeToUpdate, noticeToUpdate.getStudy().getId());
    }

    private NoticeResponse buildNoticeResponse(Notice notice, Long studyId) {
        return NoticeResponse.builder()
            .id(notice.getId())
            .content(notice.getContent())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .studyId(studyId)
            .build();
    }

    @Override
    @Transactional
    public UpdateNoticeResponse updateNotice(Long noticeId, UpdateNoticeRequest updateNoticeRequest, User user) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_NOT_FOUND));

        checkUserRole(notice.getStudy().getId(), user);
        notice.updateNotice(updateNoticeRequest.getContent());

        return buildUpdateNoticeResponse(notice);
    }

    private UpdateNoticeResponse buildUpdateNoticeResponse(Notice notice) {
        return UpdateNoticeResponse.builder()
            .id(notice.getId())
            .content(notice.getContent())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public UpdateNoticeResponse deleteNotice(Long studyId, Long noticeId, User user) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_NOT_FOUND));

        checkUserRole(studyId, user);

        notice.updateNotice("");
        notice.setDeleted(true);

        return UpdateNoticeResponse.builder()
            .id(notice.getId())
            .content(notice.getContent())
            .updatedAt(notice.getUpdatedAt())
            .isDeleted(notice.isDeleted())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(cacheNames = "getNotice")
    public NoticeResponse getNoticeByStudyIdAndNoticeId(Long studyId, Long noticeId, User user) {
        checkUserRole(studyId, user);
        Notice notice = noticeRepository.findByStudyIdAndNoticeId(studyId, noticeId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_NOT_FOUND));

        return NoticeResponse.builder()
            .id(notice.getId())
            .content(notice.getContent())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .studyId(notice.getStudy().getId())
            .build();
    }

    private void checkUserRole(Long studyId, User user) {
        Optional<RoleEnum> role = studyMemberRepository.findRoleByStudyIdAndUserId(studyId, user.getId());

        // 권한이 ROLE_LEADER 일 때 수정, 삭제 가능!
        if (role.isEmpty() || role.get() != RoleEnum.ROLE_LEADER) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }

}
