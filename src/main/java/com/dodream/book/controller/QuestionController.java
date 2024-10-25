package com.dodream.book.controller;

import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.service.QuestionService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 문제 전체 조회 (최신순/내가 푼 문제 제외 조회)
    @GetMapping("/{id}/questions")
    public ResponseEntity<Page<QuestionListResponse>> getAllQuestions(
        @PageableDefault(page = 0, size = 5, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("id") Long id) {
        Page<QuestionListResponse> questionList = questionService.getQuestions(pageable, id);
        return ResponseEntity.ok(questionList);
    }

    // 문제 개별 조회 (문제 풀기 페이지)
    @GetMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionListResponse> getQuestion(@PathVariable("id") Long id, @PathVariable("questionId") Long questionId) {
        QuestionListResponse question = questionService.getOneQuestion(id, questionId);
        return ResponseEntity.ok(question);
    }

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
