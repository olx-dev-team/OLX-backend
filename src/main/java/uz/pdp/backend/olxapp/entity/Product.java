package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.enums.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "product")
@FieldNameConstants
@SQLDelete(sql = "UPDATE product SET deleted =true WHERE id=?")
@SQLRestriction(value = "deleted=false")
public class Product extends LongIdAbstract {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean isApproved = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING_REVIEW;

    @Column(nullable = false)
    private Integer viewCounter = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "product")
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

//    @Column(nullable = false)
//    private String title;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(nullable = false)
//    private BigDecimal price;
//
//    @Column(nullable = false)
//    private Boolean isApproved = false; // Moderatsiya tekshiruvi uchun
//
//    @Column(nullable = false)
//    private Integer viewCounter = 0;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private Category category;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Favorites> favorites = new ArrayList<>();
//
//    @ManyToMany
//    @JoinTable(name = "product_attachments",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
//    private List<Attachment> attachments = new ArrayList<>();
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
}
