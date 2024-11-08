package com.dodream.book.entity;

import com.dodream.study.entity.StudyUserAnswer;
import com.dodream.user.entity.User;
import com.dodream.book.enumtype.Evaluation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Table(name = "user_answer")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(nullable = false)
    @Builder.Default
    private Evaluation evaluation = Evaluation.EVALUATION_BEFORE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "userAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUserAnswer> studyUserAnswers;

    // 문제 평가하기
    public void updateEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    // 문제 다시 풀기
    public void updateAnswer(String answer, Evaluation evaluation) {
        this.answer = answer;
        this.evaluation = evaluation;
    }
}
