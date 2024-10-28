package com.dodream.book.controller;

import com.dodream.book.domain.AddToStudiesRequest;
import com.dodream.book.domain.AnswerDetailsResponse;
import com.dodream.book.domain.EvaluationRequest;
import com.dodream.book.domain.UserAnswerRequest;
import com.dodream.book.domain.UserAnswerResponse;
import com.dodream.book.service.UserAnswerService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class UserAnswerController {
    private final UserAnswerService userAnswerService;

    // 문제 풀기
    // 만약 문제를 다시 푸는 경우 해당 answer와 evaluation이 다시 업데이트 되도록 구현 완료
    @PostMapping("/{id}/questions/{questionId}/answer")
    public ResponseEntity<UserAnswerResponse> solveQuestion(
        @PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId,
        @AuthenticationPrincipal User user,
        @RequestBody UserAnswerRequest userAnswerRequest) {
        UserAnswerResponse response = userAnswerService.solveQuestion(id, questionId, user, userAnswerRequest);
        return ResponseEntity.ok(response);
    }

    // 문제 평가하기
    @PatchMapping("/{id}/questions/{questionId}/answer/{answerId}")
    public ResponseEntity<UserAnswerResponse> evaluateAnswer(
        @PathVariable("id") Long id,
        @PathVariable("questionId") Long questionId,
        @PathVariable("answerId") Long answerId,
        @RequestBody EvaluationRequest evaluationRequest) {

        UserAnswerResponse response = userAnswerService.evaluateAnswer(answerId, evaluationRequest.getEvaluation());
        return ResponseEntity.ok(response);
    }

    // 문제 풀고 문제 조회 (문제+모범답안+내답안)
    @GetMapping("/{id}/questions/{questionId}/answer/{answerId}")
    public ResponseEntity<AnswerDetailsResponse> getAnswerDetails(
        @PathVariable("id") Long bookId,
        @PathVariable("questionId") Long questionId,
        @PathVariable("answerId") Long answerId,
        @AuthenticationPrincipal User user) {

        AnswerDetailsResponse response = userAnswerService.getAnswerDetails(bookId, questionId, answerId, user);
        return ResponseEntity.ok(response);
    }

    // 문제 푼 뒤, 내가 참여하는 스터디에 추가하기
    @PostMapping("/{id}/questions/{questionId}/studies")
    public ResponseEntity<Void> addQuestionToMyStudies(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal User user,
        @PathVariable("questionId") Long questionId,
        @RequestBody AddToStudiesRequest addToStudiesRequest // 선택한 스터디 ID 리스트
    ) {
        userAnswerService.addQuestionToMyStudies(user, questionId, addToStudiesRequest.getStudyIds());
        return ResponseEntity.ok().build();
    }
}
