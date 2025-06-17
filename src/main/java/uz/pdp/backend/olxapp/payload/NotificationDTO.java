package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Notification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for {@link Notification}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    @NotBlank(message = "Message is required")
    @Size(max = 100, message = "Message must have at least {max} characters")
    private String message;

    private boolean seen = false;

    private Long receiverId;

    private Long senderId;

}