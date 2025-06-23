package uz.pdp.backend.olxapp.mapper;

import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.Message;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;
import uz.pdp.backend.olxapp.payload.UserPublicDTO;

public interface ChatMapper {
    ChatDTO toChatDTO(Chat chat, Long currentUserId);

    MessageDTO toMessageDTO(Message message);

    UserPublicDTO toUserPublicDTO(User user);

     User getCompanion(Chat chat, Long currentUserId);
}
