package com.dodream.study.service;

import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyUserQueAnswerResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyUserQueAnswerServiceImpl implements StudyUserQueAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final StudyRepository studyRepository;

    // 전체 조회
    @Override
    @Transactional(readOnly = true)
    public Page<StudyUserQueAnswerResponse> getStudyDetails(Pageable pageable, Long studyId, User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));
        return userAnswerRepository.findStudyUserAnswers(pageable, study.getId(), user);
    }

    // 내가 푼 문제 조회
    @Override
    @Transactional(readOnly = true)
    public Page<StudyUserQueAnswerResponse> getStudyMyDetails(Pageable pageable, Long studyId,
        User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));
        return userAnswerRepository.findStudyMyUserAnswers(pageable, study.getId(), user);
    }

    // 내가 풀지 않은 문제 조회
    @Override
    @Transactional(readOnly = true)
    public Page<StudyUserQueAnswerResponse> getStudyOtherDetails(Pageable pageable, Long studyId,
        User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));
        return userAnswerRepository.findStudyOtherUserAnswers(pageable, study.getId(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyUserQueAnswerResponse> getSearchStudyUserAnswer(Pageable pageable, Long studyId,
        User user, String keyword) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));
        return userAnswerRepository.searchStudyUserAnswers(pageable, study.getId(), user, keyword);
    }
}
