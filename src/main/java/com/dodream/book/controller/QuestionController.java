package com.dodream.book.controller;

import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.service.QuestionService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    // 문제 생성
    @PostMapping("/{id}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(@PathVariable("id") Long id,
        @AuthenticationPrincipal User user, @RequestBody QuestionRequest questionRequest) {
        QuestionResponse addedQuestion = questionService.addQuestion(id, user, questionRequest);
        return ResponseEntity.ok(addedQuestion);
    }

    // 문제 삭제
    @DeleteMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> deleteQuestion(@PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId, @AuthenticationPrincipal User user) {
        questionService.deleteQuestion(id, questionId, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
