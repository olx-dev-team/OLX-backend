package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByReceiverIdAndSeenFalse(Long receiverId);

    List<Notification> findByReceiverId(Long receiverId);
}