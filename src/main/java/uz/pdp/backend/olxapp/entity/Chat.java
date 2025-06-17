package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "chat")
public class Chat extends LongIdAbstract {

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages;

    private Timestamp senderAt;
}