package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Notification;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link Notification}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean active;

    private Long id;

    private String message;

    private boolean seen = false;

    private Long receiverId;

    private Long senderId;

}