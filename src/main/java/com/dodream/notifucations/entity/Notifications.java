package com.dodream.notifucations.entity;


import com.dodream.bookcomment.entity.BookComment;
import com.dodream.quecomment.entity.QueComment;
import com.dodream.study.entity.Study;
import com.dodream.studymember.entity.StudyMember;
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
import javax.management.NotificationListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;


@Getter
@Table(name = "notification")
@Entity
@EntityListeners(NotificationListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id; // Id

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User userId; // 알림을 받을 유저 ID

    @JoinColumn(name = "book_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BookComment bookCommentId; // 문제집에 달린 댓글 ID

    @JoinColumn(name = "question_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private QueComment queCommentId; // 문제에 달린 댓글 ID

    @JoinColumn(name = "study_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StudyMember studyMemberId; // 스터디 멤버 ID

    @JoinColumn(name = "study_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Study studyId; // 스터디 ID

    @Column(nullable = false)
    private boolean is_read = false; // 읽음 여부

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime created_at; // 알림 생성 날짜
}

