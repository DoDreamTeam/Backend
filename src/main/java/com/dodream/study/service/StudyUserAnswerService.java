package com.dodream.study.service;


import com.dodream.study.domain.StudyUserAnswerResponse;

public interface StudyUserAnswerService {

    // 문제 정보 가져오기
    StudyUserAnswerResponse getStudyUserAnswer(Long id);
}
