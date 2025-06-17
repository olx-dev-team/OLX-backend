package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends LongIdAbstract {

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private Timestamp sentAt;

}