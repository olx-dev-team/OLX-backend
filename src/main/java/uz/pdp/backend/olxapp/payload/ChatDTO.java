package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Chat}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO implements Serializable {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    private Long senderId;

    private Long receiverId;

    private List<MessageDTO> messages;

    private LocalDateTime senderAt;
}