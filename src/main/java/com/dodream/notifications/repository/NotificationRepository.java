package com.dodream.notifications.repository;

import com.dodream.notifications.entity.Notifications;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notifications> findAllByCreatedAtBefore(LocalDateTime localDateTime);

}
