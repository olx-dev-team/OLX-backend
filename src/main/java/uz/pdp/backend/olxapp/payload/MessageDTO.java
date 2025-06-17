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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    @NotBlank(message = "Text is required")
    private String text;
    @Size(max = 1000, message = "Text must have maximum {max} characters")

    private Long chatId;

    private Long senderID;

    private LocalDateTime sentAt;

}