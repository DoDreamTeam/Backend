package com.dodream.book.controller;

import com.dodream.book.domain.AddToMyBooksRequest;
import com.dodream.book.domain.AddToMyBooksResponse;
import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.service.QuestionService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    // 문제 전체 조회 (최신순 조회)
    @GetMapping("/{id}/questions")
    public ResponseEntity<Page<QuestionListResponse>> getAllQuestions(
        @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @PathVariable("id") Long bookId,
        @AuthenticationPrincipal User user
    ) {
        // 최신순으로만 조회
        Page<QuestionListResponse> questionList = questionService.getQuestions(pageable, bookId, user);
        return ResponseEntity.ok(questionList);
    }


    // 내가 푼 문제 제외 조회하기
    @GetMapping("/{id}/questions/my")
    public ResponseEntity<Page<QuestionListResponse>> getMyQuestions(
        @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
        @PathVariable("id") Long bookId,
        @AuthenticationPrincipal User user
    ) {
        Page<QuestionListResponse> questionList = questionService.getQuestionsExceptMy(pageable, bookId, user);
        return ResponseEntity.ok(questionList);
    }

    // 문제 개별 조회 (문제 풀기 페이지)
    @GetMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable("id") Long id, @PathVariable("questionId") Long questionId) {
        QuestionResponse question = questionService.getOneQuestion(id, questionId);
        return ResponseEntity.ok(question);
    }

    // 문제 생성
    @PostMapping("/{id}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(@PathVariable("id") Long id,
        @AuthenticationPrincipal User user, @RequestBody QuestionRequest questionRequest) {
        QuestionResponse addedQuestion = questionService.addQuestion(id, user, questionRequest);
        return ResponseEntity.ok(addedQuestion);
    }

    // 문제 수정 (문제, 모범답안 둘 다 수정 가능)
    @PatchMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId, @AuthenticationPrincipal User user,
        @RequestBody QuestionRequest questionRequest) {
        QuestionResponse response = questionService.updateQuestion(id, questionId, questionRequest, user);
        return ResponseEntity.ok(response);
    }

    // 문제 삭제
    @DeleteMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> deleteQuestion(@PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId, @AuthenticationPrincipal User user) {
        questionService.deleteQuestion(id, questionId, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 문제를 내 문제집에 추가
    @PostMapping("/{id}/questions/{questionId}/books")
    public ResponseEntity<AddToMyBooksResponse> addQuestionToBooks(
        @AuthenticationPrincipal User user,
        @PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId,
        @RequestBody AddToMyBooksRequest request) {

        AddToMyBooksResponse response = questionService.addQuestionToBooks(id, questionId, request, user);
        return ResponseEntity.ok(response);
    }

    // 문제 제목으로 검색해서 결과 조회
    @GetMapping("/{id}/questions/search")
    public ResponseEntity<Page<QuestionListResponse>> searchQuestions(
        @PathVariable("id") Long id,
        @RequestParam("keyword") String keyword, Pageable pageable) {
        Page<QuestionListResponse> questions = questionService.searchQuestions(id, keyword, pageable);
        return ResponseEntity.ok(questions);
    }

}
