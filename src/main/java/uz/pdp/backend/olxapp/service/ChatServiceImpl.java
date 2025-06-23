package uz.pdp.backend.olxapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Chat;
import uz.pdp.backend.olxapp.entity.Message;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.exception.IllegalActionException;
import uz.pdp.backend.olxapp.mapper.ChatMapperImpl;
import uz.pdp.backend.olxapp.payload.ChatDTO;
import uz.pdp.backend.olxapp.payload.CreateMessageDTO;
import uz.pdp.backend.olxapp.payload.MessageDTO;
import uz.pdp.backend.olxapp.repository.ChatRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;
import uz.pdp.backend.olxapp.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Avazbek on 23/06/25 20:22
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatMapperImpl chatMapperImpl;


    /**
     * Находит существующий чат между пользователем и владельцем продукта или создает новый.
     * Этот метод вызывается, когда пользователь нажимает "Написать продавцу".
     *
     * @param initiatorId ID пользователя, который инициирует чат (покупатель).
     * @param productId   ID продукта, по которому идет общение.
     * @return DTO найденного или созданного чата.
     * @throws EntityNotFoundException  если продукт или пользователь не найдены.
     * @throws IllegalArgumentException если пользователь пытается начать чат с самим собой.
     */

    @Override
    @Transactional
    public ChatDTO getOrCreateChat(Long initiatorId, Long productId) {

        Product product = productRepository.
                findById(productId).
                orElseThrow(
                        () -> new EntityNotFoundException("Product with" + productId + " not found", HttpStatus.NOT_FOUND));

        User productOwner = product.getCreatedBy();

        if (initiatorId.equals(productOwner.getId()))
            throw new IllegalActionException("You can't chat with yourself", HttpStatus.BAD_REQUEST);

        User initiator = userRepository
                .findById(initiatorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with" + initiatorId + " not found", HttpStatus.NOT_FOUND));

        Chat chat = chatRepository.findChatByUsersAndProduct(initiator.getId(), productOwner.getId(), product.getId()).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.setUserOne(initiator);   // Покупатель
            newChat.setUserTwo(productOwner); // Продавец
            newChat.setProduct(product);
            return chatRepository.save(newChat);
        });

        return chatMapperImpl.toChatDTO(chat, initiatorId);


    }

    /**
     * Возвращает список всех чатов для указанного пользователя, отсортированный по последнему сообщению.
     *
     * @param userId ID пользователя, для которого нужно получить чаты.
     * @return Список ChatDTO.
     */


    @Override
    @Transactional
    public List<ChatDTO> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findAllByUserId(userId);

        return chats
                .stream()
                .map(
                        chat -> chatMapperImpl.toChatDTO(chat, userId))
                .sorted(Comparator.comparing(

                        chatDTO -> Optional.ofNullable(chatDTO.getLastMessage())
                                .map(MessageDTO::getSentAt).orElse(null), Comparator.nullsLast(
                                Comparator.reverseOrder()
                        ))
                ).collect(Collectors.toList());
    }

    /**
     * Отправляет новое сообщение в указанный чат.
     *
     * @param chatId           ID чата, в который отправляется сообщение.
     * @param senderId         ID отправителя.
     * @param createMessageDTO DTO с текстом сообщения.
     * @return DTO созданного сообщения.
     * @throws EntityNotFoundException если чат или отправитель не найдены.
     * @throws SecurityException       если отправитель не является участником чата.
     */

    @Transactional
    public MessageDTO sendMessage(Long chatId, Long senderId, CreateMessageDTO createMessageDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + chatId, HttpStatus.NOT_FOUND));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with id: " + senderId, HttpStatus.NOT_FOUND));


        if (!chat.getUserOne().getId().equals(senderId) && !chat.getUserTwo().getId().equals(senderId)) {
            throw new SecurityException("User " + senderId + " is not a participant of chat " + chatId);
        }

        Message newMessage = new Message();
        newMessage.setChat(chat);
        newMessage.setSender(sender);
        newMessage.setContent(createMessageDTO.getContent());

        chat.getMessages().add(newMessage);
        chatRepository.save(chat);

        return chatMapperImpl.toMessageDTO(newMessage);
    }

    /**
     * Получает все сообщения из конкретного чата.
     * Важно: также помечает все непрочитанные сообщения, адресованные текущему пользователю, как прочитанные.
     *
     * @param chatId ID чата для получения сообщений.
     * @param userId ID текущего пользователя (для проверки доступа и маркировки сообщений).
     * @return Список MessageDTO, отсортированный по времени отправки.
     * @throws EntityNotFoundException если чат не найден.
     * @throws SecurityException       если пользователь не является участником чата.
     */
    @Transactional // Не readOnly, так как мы можем изменять флаг isRead
    public List<MessageDTO> getChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + chatId , HttpStatus.NOT_FOUND));

        // Еще одна проверка безопасности
        if (!chat.getUserOne().getId().equals(userId) && !chat.getUserTwo().getId().equals(userId)) {
            throw new SecurityException("User " + userId + " is not a participant of chat " + chatId);
        }

        // Помечаем сообщения как прочитанные
        chat.getMessages().stream()
                .filter(message -> !message.getSender().getId().equals(userId) && !message.isRead())
                .forEach(message -> message.setRead(true));

        chatRepository.save(chat); // Сохраняем изменения (флаги isRead)

        // Возвращаем отсортированный список всех сообщений
        return chat.getMessages().stream()
                .map(chatMapperImpl::toMessageDTO)
                .sorted(Comparator.comparing(MessageDTO::getSentAt))
                .collect(Collectors.toList());
    }


}