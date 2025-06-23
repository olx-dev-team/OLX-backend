package uz.pdp.backend.olxapp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.Message;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;
import uz.pdp.backend.olxapp.payload.UserPublicDTO;

import java.util.Comparator;
import java.util.Optional;

/**
 * Created by Avazbek on 23/06/25 22:25
 */
@Component
public class ChatMapperImpl implements ChatMapper {

    public ChatDTO toChatDTO(Chat chat, Long currentUserId) {
        User companionEntity = getCompanion(chat, currentUserId);
        UserPublicDTO companionDTO = toUserPublicDTO(companionEntity);

        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(chat.getId());
        chatDTO.setProductId(chat.getProduct().getId());
        chatDTO.setCompanion(companionDTO);

        // Находим и маппим последнее сообщение
        Optional<Message> lastMessageOpt = chat.getMessages().stream()
                .max(Comparator.comparing(Message::getSentAt));
        lastMessageOpt.ifPresent(message -> chatDTO.setLastMessage(toMessageDTO(message)));

        return chatDTO;
    }

    public MessageDTO toMessageDTO(Message message) {
        if (message == null) {
            return null;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setSentAt(message.getSentAt());
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setRead(message.isRead());
        return messageDTO;
    }

    public UserPublicDTO toUserPublicDTO(User user) {
        if (user == null) {
            return null;
        }
        UserPublicDTO userDTO = new UserPublicDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getFirstName()); // Предположим, имя берется из поля firstName
        return userDTO;
    }

    public User getCompanion(Chat chat, Long currentUserId) {
        return chat.getUserOne().getId().equals(currentUserId)
                ? chat.getUserTwo()
                : chat.getUserOne();
    }

}
