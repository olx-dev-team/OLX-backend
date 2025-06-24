package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Chat}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO implements Serializable {

    private Long id;

    private Long productId;

    private UserPublicDTO companion;

    private MessageDTO lastMessage;
}