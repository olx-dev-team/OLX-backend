package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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