package com.dodream.study.service;

import com.dodream.study.domain.StudyUserQueAnswerResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyUserQueAnswerService {

    Page<StudyUserQueAnswerResponse> getStudyDetails(Pageable pageable, Long studyId, User user);

    Page<StudyUserQueAnswerResponse> getStudyMyDetails(Pageable pageable, Long studyId, User user);

    Page<StudyUserQueAnswerResponse> getStudyOtherDetails(Pageable pageable, Long studyId, User user);

    Page<StudyUserQueAnswerResponse> getSearchStudyUserAnswer(Pageable pageable, Long studyId, User user, String keyword);
}
