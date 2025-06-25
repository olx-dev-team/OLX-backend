package uz.pdp.backend.olxapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.CreateMessageDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;

import java.util.List;

public interface ChatService {

    ChatDTO getOrCreateChat(Long initiatorId, Long productId);

    List<ChatDTO> getUserChats(Long userId);

    MessageDTO sendMessage(Long chatId, Long senderId, CreateMessageDTO createMessageDTO);

    PageDTO<MessageDTO> getChatMessages(Long chatId, Long userId, Pageable pageable);

}