package com.dodream.study.controller;

import com.dodream.study.domain.StudyUserAnswerResponse;
import com.dodream.study.service.StudyUserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/answer")
public class StudyUserAnswerController {

    private final StudyUserAnswerService studyUserAnswerService;

    @GetMapping("{id}")
    public ResponseEntity<StudyUserAnswerResponse> getResponse(@PathVariable("id") Long id) {
        StudyUserAnswerResponse studyUserAnswer = studyUserAnswerService.getStudyUserAnswer(id);
        return ResponseEntity.ok(studyUserAnswer);
    }
}
