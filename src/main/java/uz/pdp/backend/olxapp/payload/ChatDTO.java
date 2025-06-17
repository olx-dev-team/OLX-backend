package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Message;
import uz.pdp.backend.olxapp.entity.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Chat}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO implements Serializable {

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean active;

    private Long id;

    private Long senderId;

    private Long receiverId;

    private List<MessageDTO> messages;

    private Timestamp senderAt;
}