package com.dodream.study.service;

import com.dodream.study.domain.StudyMemberRequest;
import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.domain.StudyMemberUpdateRequest;
import com.dodream.study.domain.StudyMemberUpdateResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyMemberService {
    Page<StudyMemberResponse> getStudyMembers(Long studyId, User user, Pageable pageable);
    Page<StudyMemberResponse> getStudyApplyMembers(Long studyId, User user, Pageable pageable);
    StudyMemberResponse addStudyMember(User user, StudyMemberRequest studyMemberRequest);
    StudyMemberUpdateResponse updateStudyMember(User user, Long memberId, StudyMemberUpdateRequest studyMemberUpdateRequest);
    void deleteStudyMember(User user, Long memberId);
    StudyMemberUpdateResponse transferLeader(User user, Long currentLeaderId, Long newLeaderId);
}
