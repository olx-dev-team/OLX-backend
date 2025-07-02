package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.NotificationDTO;
import uz.pdp.backend.olxapp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/close/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Test successfully
     *
     * @param user The currently authenticated user
     * @return list of notifications for this user
     */
    //  barcha bildirishnomalarni olish
    @Operation(
            summary = "Get all notifications for current user",
            description = "Returns a list of all notifications associated with the authenticated user"
    )
    @GetMapping("/user")
    public List<NotificationDTO> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) throw new RuntimeException("You are not authorized");
        return notificationService.getNotificationsForUser(user.getId());
    }


    //  yangi bildirishnomalar sonini olish agar seen-> false bolsa sanaydi
    @Operation(
            summary = "Get count of unread notifications",
            description = "Returns the number of unread notifications (where `seen=false`) for the authenticated user"
    )
    @GetMapping("/user/unread-count")
    public Long getUnreadCount(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return notificationService.countUnread(user.getId());
    }


    //  ko‘rildi deb belgilash
    @Operation(
            summary = "Mark a notification as seen",
            description = "Marks a specific notification as seen/read using its ID"
    )
    @GetMapping("/{notificationId}/mark-seen")
    public void markAsSeen(
            @Parameter(description = "Notification ID to mark as seen", example = "1")
            @PathVariable Long notificationId) {
        notificationService.markAsSeen(notificationId);
    }

    //emailga xabar jonatish
    @Operation(
            summary = "Send notification by email",
            description = "Sends a notification email to the user with the given receiver ID"
    )
    @GetMapping("/send-email/{receiverId}")
    public void sendNotification(
            @Parameter(description = "Receiver user ID to send the notification to", example = "5")
            @PathVariable Long receiverId) {
        notificationService.sendNotificationByReceiver(receiverId);
    }
}
