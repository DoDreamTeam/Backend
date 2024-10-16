package com.dodream.study.service;

import com.dodream.study.domain.StudyResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.repository.StudyRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;
//    private final StudyMemberRepository studyMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> getStudyList() {
        List<Study> studyList = studyRepository.findAll();
        return studyList.stream().map(s -> {
                String status = null;               // 기본 상태값
//                if (authentication) {
//                    // 로그인 사용자 정보를 통해, studyMember에서 역할 확인
//                    status = studyMemberRepository.findRoleBystudyAnduser(s.getId(), authentication.getUser().getuser());
//                }
                return StudyResponse.builder()
                    .id(s.getId())
                    .category(s.getCategory().getCategory())
                    .status(status) // 역할 설정 (FE에서 null이면 "신청하기", LEADER OR MEMBER 이면 "참여중", WAITING이면 "승인대기중" 표시)
                    .title(s.getTitle())
                    .description(s.getDescription())
                    .username(s.getUser().getUsername())
                    .build();
            }
        ).toList();
    }
}
