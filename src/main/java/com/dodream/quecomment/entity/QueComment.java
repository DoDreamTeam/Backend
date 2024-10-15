package com.dodream.quecomment.entity;

import com.dodream.studyanswer.entity.StudyAnswer;
import com.dodream.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Table(name = "question_comment")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id; // ID

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User userId; // 댓글을 작성한 사용자 ID

    @JoinColumn(name = "study_answer_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private StudyAnswer studyAnswerId; // 스터디에 추가한 문제 ID

    @Column(nullable = false)
    private String content; // 댓글 내용

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 댓글 생성 날짜

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 댓글 수정 날짜

}