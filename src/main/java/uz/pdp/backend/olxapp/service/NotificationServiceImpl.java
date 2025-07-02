package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Notification;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.NotificationMapper;
import uz.pdp.backend.olxapp.payload.NotificationDTO;
import uz.pdp.backend.olxapp.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    @Override
    public List<NotificationDTO> getNotificationsForUser(Long receiverId) {

        List<Notification> notifications = notificationRepository
                .findByReceiverId(receiverId);

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO dto = notificationMapper.toDto(notification);

            if (notification.getSender() != null)
                dto.setSenderId(notification.getSender().getId());
            if (notification.getReceiver() != null)
                dto.setReceiverId(notification.getReceiver().getId());

            notificationDTOS.add(dto);
        }
        return notificationDTOS;
    }

    @Override
    public Long countUnread(Long receiverId) {
        Long count = notificationRepository.countByReceiverIdAndSeenFalse(receiverId);
        log.info("Unread notifications for receiverId={}: {}", receiverId, count);
        return count;
    }

    @Override
    public void markAsSeen(Long notificationId) {
        log.info("Marking notification as seen: notificationId={}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Notification not found with id={}", notificationId);
                    return new EntityNotFoundException("Notification not found with id:" + notificationId, HttpStatus.NOT_FOUND);
                });

        notification.setSeen(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        log.debug("Notification marked as seen: notificationId={}", notificationId);
    }

    @Override
    public void sendNotificationByReceiver(Long receiverId) {
        log.info("Sending email notifications to receiverId={}", receiverId);

        List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);

        for (Notification notification : notifications) {
            User receiver = notification.getReceiver();

            try {
                emailService.sendSimpleEmail(receiver.getEmail(),
                        "Yangi xabar keldi sizga ",
                        notification.getMessage());

                log.debug("Email sent to {} for notificationId={}", receiver.getEmail(), notification.getId());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", receiver.getEmail(), e.getMessage());
            }
        }

        log.info("Finished sending {} notifications to receiverId={}", notifications.size(), receiverId);
    }
}
