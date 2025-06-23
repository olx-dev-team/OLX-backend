package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.backend.olxapp.entity.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("SELECT c FROM chats c WHERE c.product.id = :advertisementId " +
            "AND ((c.userOne.id = :userOneId AND c.userTwo.id = :userTwoId) " +
            "OR (c.userOne.id = :userTwoId AND c.userTwo.id = :userOneId))")
    Optional<Chat> findChatByUsersAndProduct(Long userOneId, Long userTwoId, Long advertisementId);

    @Query("SELECT c FROM chats c WHERE c.userOne.id = :userId OR c.userTwo.id = :userId")
    List<Chat> findAllByUserId(Long userId);
}