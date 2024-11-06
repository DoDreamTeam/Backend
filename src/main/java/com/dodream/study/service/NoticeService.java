package com.dodream.study.service;

import com.dodream.study.domain.NoticeRequest;
import com.dodream.study.domain.NoticeResponse;
import com.dodream.study.domain.UpdateNoticeRequest;
import com.dodream.study.domain.UpdateNoticeResponse;
import com.dodream.user.entity.User;

public interface NoticeService {
    NoticeResponse createNotice(Long studyId, NoticeRequest noticeRequest, User user);
    UpdateNoticeResponse updateNotice(Long noticeId, UpdateNoticeRequest updateNoticeRequest, User user);
    UpdateNoticeResponse deleteNotice(Long studyId, Long noticeId, User user);
    NoticeResponse getNoticeByStudyId(Long studyId, User user);
}
