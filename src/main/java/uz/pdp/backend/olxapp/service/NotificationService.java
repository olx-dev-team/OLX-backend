package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.NotificationDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getNotificationsForUser(Long receiverId);

    Long countUnread(Long receiverId);

    void markAsSeen(Long notificationId);

    void sendNotificationByReceiver(Long receiverId);
}
