package com.dodream.study.service;

import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.domain.StudyRequest;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.domain.StudyUpdateRequest;
import com.dodream.study.domain.StudyUpdateResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface StudyService {
    Page<StudyResponse> getStudyList(User loginedUser, Pageable pageable, String category);
    Page<StudyResponse> searchStudiesByKeyword(Pageable pageable, User user, String keyword);
    StudyResponse addStudy(User user, StudyRequest studyRequest);
    void deleteStudy(User user, Long id);
    StudyUpdateResponse updateStudy(User user, Long id, StudyUpdateRequest studyUpdateRequest);
    Page<StudyResponse> getMyStudyList(Pageable pageable, User user);
    Page<StudyResponse> getPopularStudyList(Pageable pageable, User user, Long userCount);
    StudyResponse findStudy(Long id, User user);
    String existStudy(Long id, User user);

//    Page<StudyResponse> getMyStudy(User user, Pageable pageable, Long id);
}
