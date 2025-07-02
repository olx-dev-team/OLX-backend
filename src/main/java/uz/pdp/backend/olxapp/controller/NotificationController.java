package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.User;
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

    /**
     * Test successfully
     *
     * @param receiverId - with user id
     * @return list of notifications for this user
     */
    //  barcha bildirishnomalarni olish
    @GetMapping("/user")
    public List<NotificationDTO> getNotifications(@AuthenticationPrincipal User user) {
        if (user == null) throw new RuntimeException("You are not authorized");
        return notificationService.getNotificationsForUser(user.getId());
    }


    //  yangi bildirishnomalar sonini olish agar seen-> false bolsa sanaydi
    @GetMapping("/user/unread-count")
    public Long getUnreadCount(@AuthenticationPrincipal User user) {
        return notificationService.countUnread(user.getId());
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


}
