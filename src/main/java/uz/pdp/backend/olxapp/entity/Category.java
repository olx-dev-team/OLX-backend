package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Category extends LongIdAbstract {

    @Column(nullable = false, unique = true)
    private String name;

    // Self-referencing for subcategories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}
