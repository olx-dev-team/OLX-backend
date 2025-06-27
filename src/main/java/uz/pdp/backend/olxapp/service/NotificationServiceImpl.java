package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
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
        return notificationRepository
                .countByReceiverIdAndSeenFalse(receiverId);
    }

    @Override
    public void markAsSeen(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id:" + notificationId, HttpStatus.NOT_FOUND));

        notification.setSeen(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void sendNotificationByReceiver(Long receiverId) {

        List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);

        for (Notification notification : notifications) {
            User receiver = notification.getReceiver();

            emailService.sendSimpleEmail(receiver.getEmail(),
                    "Yangi xabar keldi sizga ",
                    notification.getMessage());
        }
    }

}
