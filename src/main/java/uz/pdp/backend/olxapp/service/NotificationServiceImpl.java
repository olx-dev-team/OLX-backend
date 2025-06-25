package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.backend.olxapp.entity.Notification;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.exception.NotificationNotFoundException;
import uz.pdp.backend.olxapp.mapper.NotificationMapper;
import uz.pdp.backend.olxapp.payload.NotificationDTO;
import uz.pdp.backend.olxapp.repository.NotificationRepository;
import uz.pdp.backend.olxapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    @Override
    public List<NotificationDTO> getNotificationsForUser(Long receiverId) {

        List<Notification> notifications = notificationRepository
                .findByReceiverId(receiverId);

        List<NotificationDTO> dtos = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO dto = notificationMapper.toDto(notification);

            if (notification.getSender() != null)
                dto.setSenderId(notification.getSender().getId());
            if (notification.getReceiver() != null)
                dto.setReceiverId(notification.getReceiver().getId());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public Long countUnread(Long receiverId) {
        return notificationRepository
                .countByReceiverIdAndSeenFalse(receiverId);
    }

    @Override
    public void markAsSeen(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id:" + notificationId));

        notification.setSeen(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void sendNotificationByReceiver(Long receiverId) {

        List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);

        for (Notification notification : notifications) {
            User receiver = notification.getReceiver();

            emailService.sendSimpleMessage(receiver.getEmail(),
                    "Yangi xabar keldi sizga ",
                    notification.getMessage());
        }
    }

}
