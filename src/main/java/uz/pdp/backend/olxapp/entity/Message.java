package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends LongIdAbstract {

    private String text;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private Timestamp sentAt;

}