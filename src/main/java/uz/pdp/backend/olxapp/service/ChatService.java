package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.CreateMessageDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;

import java.util.List;

public interface ChatService {

    ChatDTO getOrCreateChat(Long initiatorId, Long productId);

    List<ChatDTO> getUserChats(Long userId);

    MessageDTO sendMessage(Long chatId, Long senderId, CreateMessageDTO createMessageDTO);

    List<MessageDTO> getChatMessages(Long chatId, Long userId);

}