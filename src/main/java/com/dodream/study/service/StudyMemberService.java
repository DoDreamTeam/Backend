package com.dodream.study.service;

import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyMemberService {
    Page<StudyMemberResponse> getStudyMembers(Long studyId, User user, Pageable pageable);
    Page<StudyMemberResponse> getStudyApplyMembers(Long studyId, User user, Pageable pageable);
}
