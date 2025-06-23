package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

@Entity
@Table(name = "product_image", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "is_main"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends LongIdAbstract {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;

    @Column(name = "is_main", nullable = false)
    private boolean isMain = false;
}