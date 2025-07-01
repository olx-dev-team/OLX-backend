package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "chats")
@FieldNameConstants
public class Chat extends LongIdAbstract {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_one_id")
    private User userOne; // Покупатель

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_two_id")
    private User userTwo; // Продавец (владелец продукта)


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

}