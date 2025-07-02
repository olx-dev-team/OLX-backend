package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.Message;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.CreateMessageDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.ChatService;

import java.util.List;

/**
 * Created by Avazbek on 24/06/25 10:27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/close/v1/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * Test successfully
     *
     * @param productId   - with product id
     * @param currentUser - security context current user
     * @return response entity with chat dto
     */
    @PostMapping
    public ResponseEntity<ChatDTO> getOrCreateChat(@RequestParam Long productId,
                                                   @AuthenticationPrincipal User currentUser) {
        // ID пользователя получаем из Security Context, а не из запроса. Это безопасно.

        ChatDTO chat = chatService.getOrCreateChat(currentUser.getId(), productId);
        // Возвращаем 200 OK, так как эндпоинт и "получает" и "создает".
        return ResponseEntity.ok(chat);
    }

    /**
     * Test successfully
     *
     * @param currentUser - security context current user
     * @return response entity with all chats of this user
     */
    @GetMapping
    public ResponseEntity<List<ChatDTO>> getUserChats(@AuthenticationPrincipal User currentUser) {

        List<ChatDTO> userChats = chatService.getUserChats(currentUser.getId());
        return ResponseEntity.ok(userChats);
    }

    /**
     * Test successfully
     *
     * @param chatId           - chat id to send message in it
     * @param createMessageDTO - message data transfer object
     * @param currentUser      - security context current user
     * @return response entity with created message dto
     */
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long chatId,
                                                  @Valid @RequestBody CreateMessageDTO createMessageDTO,
                                                  @AuthenticationPrincipal User currentUser) {
        MessageDTO sentMessage = chatService.sendMessage(chatId, currentUser.getId(), createMessageDTO);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    /**
     * Test successfully
     *
     * @param chatId      - chat id to get messages from it
     * @param currentUser - security context current user
     * @param page        - page number default 0
     * @param size        - page size default 10
     * @return response entity with page of messages
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<PageDTO<MessageDTO>> getChatMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal User currentUser,
            // Spring автоматически создаст Pageable из параметров запроса
            // ?page=0&size=20&sort=sentAt,desc
//            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable)
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, Message.Fields.sentAt);
        PageRequest pageable = PageRequest.of(page, size, sort);


        PageDTO<MessageDTO> messages = chatService.getChatMessages(chatId, currentUser.getId(), pageable);
        return ResponseEntity.ok(messages);
    }


}
