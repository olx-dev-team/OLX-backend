package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}