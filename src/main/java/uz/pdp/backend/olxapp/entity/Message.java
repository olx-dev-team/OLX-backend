package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message extends LongIdAbstract {
    // К какому чату относится сообщение
    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    // Кто отправил это конкретное сообщение
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    // Очень полезный флаг для UI
    private boolean isRead = false;
}