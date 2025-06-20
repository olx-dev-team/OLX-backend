package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "notification")
public class Notification extends LongIdAbstract {

    @Column(nullable = false)
    private String message;

    private boolean seen = false;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

}