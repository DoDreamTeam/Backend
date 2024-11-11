package com.dodream.study.service;

import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyUserQueAnswerResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import com.dodream.util.CustomPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
//    @Cacheable(value = "studyDetails",
//        key = "#studyId + '_' + #user.id + '_' + #pageable.pageNumber",
//        unless = "#result.isEmpty()")
    public Page<StudyUserQueAnswerResponse> getStudyDetails(Pageable pageable, Long studyId, User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        Page<StudyUserQueAnswerResponse> studyUserAnswers = userAnswerRepository.findStudyUserAnswers(pageable, study.getId(), user);
        return new PageImpl<>(studyUserAnswers.getContent(), pageable, studyUserAnswers.getTotalElements());
    }

    // 내가 푼 문제 조회
    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "myStudyDetails",
//        key = "#studyId + '_' + #user.id + '_' + #pageable.pageNumber",
//        unless = "#result.isEmpty()")
    public Page<StudyUserQueAnswerResponse> getStudyMyDetails(Pageable pageable, Long studyId,
        User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        Page<StudyUserQueAnswerResponse> myStudyUserAnswers = userAnswerRepository.findStudyMyUserAnswers(pageable, study.getId(), user);
        return new PageImpl<>(myStudyUserAnswers.getContent(), pageable, myStudyUserAnswers.getTotalElements());
    }

    // 내가 풀지 않은 문제 조회
    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "otherStudyDetails",
//        key = "#studyId + '_' + #user.id + '_' + #pageable.pageNumber",
//        unless = "#result.isEmpty()")
    public Page<StudyUserQueAnswerResponse> getStudyOtherDetails(Pageable pageable, Long studyId,
        User user) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        Page<StudyUserQueAnswerResponse> otherStudyUserAnswers = userAnswerRepository.findStudyOtherUserAnswers(pageable, study.getId(), user);
        return new PageImpl<>(otherStudyUserAnswers.getContent(), pageable, otherStudyUserAnswers.getTotalElements());
    }

    // 특정 검색어로 문제 조회
    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "searchStudyUserAnswer",
//        key = "#studyId + '_' + #user.id + '_' + #keyword + '_' + #pageable.pageNumber",
//        unless = "#result.isEmpty()")
    public Page<StudyUserQueAnswerResponse> getSearchStudyUserAnswer(Pageable pageable, Long studyId,
        User user, String keyword) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        Page<StudyUserQueAnswerResponse> searchResults = userAnswerRepository.searchStudyUserAnswers(pageable, study.getId(), user, keyword);
        return new PageImpl<>(searchResults.getContent(), pageable, searchResults.getTotalElements());
    }
}
