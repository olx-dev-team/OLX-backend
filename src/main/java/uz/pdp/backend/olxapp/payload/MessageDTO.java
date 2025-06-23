package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Message}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {

    private Long id;

    private String content;

    private LocalDateTime sentAt;

    private Long senderId;

    private boolean isRead;

}