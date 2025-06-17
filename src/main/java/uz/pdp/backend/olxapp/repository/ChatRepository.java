package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}