package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.NotificationDTO;
import uz.pdp.backend.olxapp.service.NotificationService;
import uz.pdp.backend.olxapp.service.UserService;
import uz.pdp.backend.olxapp.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/close/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //  barcha bildirishnomalarni olish
    @GetMapping("/user/{receiverId}")
    public List<NotificationDTO> getNotifications(@PathVariable Long receiverId) {
        return notificationService.getNotificationsForUser(receiverId);
    }


    //  yangi bildirishnomalar sonini olish agar seen-> false bolsa sanaydi
    @GetMapping("/user/{receiverId}/unread-count")
    public Long getUnreadCount(@PathVariable Long receiverId) {
        return notificationService.countUnread(receiverId);
    }


    //  ko‘rildi deb belgilash
    @GetMapping("/{notificationId}/mark-seen")
    public void markAsSeen(@PathVariable Long notificationId) {
        notificationService.markAsSeen(notificationId);
    }

    //emailga xabar jonatish
    @GetMapping("/send-email/{receiverId}")
    public void sendNotification(@PathVariable Long receiverId) {
        notificationService.sendNotificationByReceiver(receiverId);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("123");
        System.out.println(hashedPassword);
    }

}
