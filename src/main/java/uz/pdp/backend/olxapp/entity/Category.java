package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "active"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SQLDelete(sql = "UPDATE product SET active = false WHERE id = ?")
@Where(clause = "active=true")
@FieldNameConstants
public class Category extends LongIdAbstract {

    @Column(nullable = false)
    private String name;

    // Self-referencing for subcategories
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @ToString.Exclude
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}
