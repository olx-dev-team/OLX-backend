package uz.pdp.backend.olxapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);
}