package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SQLDelete(sql = "UPDATE product SET active = false WHERE id = ?")
@Where(clause = "active=true")
public class Category extends LongIdAbstract {

    @Column(nullable = false, unique = true)
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
