package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.pdp.backend.olxapp.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByReceiverIdAndSeenFalse(Long receiverId);

    List<Notification> findByReceiverId(Long receiverId);
}