package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
@Tag(name = "Chat Controller", description = "Handles chat and messaging operations")
public class ChatController {

    private final ChatService chatService;

    /**
     * Test successfully
     *
     * @param productId   - with product id
     * @param currentUser - security context current user
     * @return response entity with chat dto
     */
    @Operation(
            summary = "Get or create a chat with seller",
            description = "Creates a new chat or returns an existing one between the authenticated user and the seller of the given product",
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product to initiate a chat with the seller", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chat created or found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatDTO.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ChatDTO> getOrCreateChat(@RequestParam Long productId,
                                                   @AuthenticationPrincipal User currentUser) {
        ChatDTO chat = chatService.getOrCreateChat(currentUser.getId(), productId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Test successfully
     *
     * @param currentUser - security context current user
     * @return response entity with all chats of this user
     */
    @Operation(
            summary = "Get all chats of current user",
            description = "Returns a list of all chats the authenticated user is participating in",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of user's chats",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping
    public ResponseEntity<List<ChatDTO>> getUserChats(@AuthenticationPrincipal User currentUser) {
        List<ChatDTO> userChats = chatService.getUserChats(currentUser.getId());
        return ResponseEntity.ok(userChats);
    }

    /**
     * Test successfully
     *
     * @param chatId           - chat id to send the message in it
     * @param createMessageDTO - message data transfer object
     * @param currentUser      - security context current user
     * @return response entity with created message dto
     */
    @Operation(
            summary = "Send a message in chat",
            description = "Sends a message to a chat. The authenticated user must be a participant in the chat.",
            parameters = {
                    @Parameter(name = "chatId", description = "ID of the chat where message will be sent", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Message payload",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateMessageDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "text": "Hello, I'm interested in your product!"
                    }
                """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Message successfully sent",
                            content = @Content(schema = @Schema(implementation = MessageDTO.class))),
                    @ApiResponse(responseCode = "403", description = "User not allowed to write in this chat")
            }
    )
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long chatId,
                                                  @Valid @org.springframework.web.bind.annotation.RequestBody CreateMessageDTO createMessageDTO,
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
    @Operation(
            summary = "Get paginated messages from chat",
            description = "Retrieves messages from the specified chat, paginated and sorted by date (descending)",
            parameters = {
                    @Parameter(name = "chatId", description = "ID of the chat", required = true),
                    @Parameter(name = "page", description = "Page number", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of messages",
                            content = @Content(schema = @Schema(implementation = PageDTO.class)))
            }
    )
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<PageDTO<MessageDTO>> getChatMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, Message.Fields.sentAt);
        PageRequest pageable = PageRequest.of(page, size, sort);

        PageDTO<MessageDTO> messages = chatService.getChatMessages(chatId, currentUser.getId(), pageable);
        return ResponseEntity.ok(messages);
    }
}
