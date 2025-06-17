package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.User;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Message}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean active;

    private Long id;

    private String text;

    private Long chatId;

    private Long senderID;

    private Timestamp sentAt;

}