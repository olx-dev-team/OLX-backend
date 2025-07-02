package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.RejectionReasonEnum;
import uz.pdp.backend.olxapp.enums.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private boolean isApproved = false; // Moderatsiya tekshiruvi uchun

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING_REVIEW;

    @ElementCollection(targetClass = RejectionReasonEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_rejection_reasons", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private Set<RejectionReasonEnum> rejectionReasons = new HashSet<>();

    @Column(nullable = false)
    private Integer viewCounter = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne( optional = false)
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
