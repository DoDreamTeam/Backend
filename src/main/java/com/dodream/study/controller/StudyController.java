package com.dodream.study.controller;

import com.dodream.study.domain.StudyResponse;
import com.dodream.study.service.StudyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping("")
    public ResponseEntity<List<StudyResponse>> getStudyList() {
        // 스프링 시큐리티를 통해 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        List<StudyResponse> result = studyService.getStudyList(authentication);
        List<StudyResponse> result = studyService.getStudyList();
        return ResponseEntity.ok(result);
    }

}
