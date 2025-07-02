package uz.pdp.backend.olxapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.enums.Status;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    //    Page<Product> findAllByIsApprovedTrueAndActiveTrue(Boolean isApproved, boolean active, Pageable pageable); // todo bu method qanday ishlaydi ??

    @Query("select p from product p where p.status=:status")
    Page<Product> findAllByStatus(Pageable pageable, @Param("status") Status status);

//    Optional<Product> findByIdAndIsApprovedTrueAndActiveTrue(Long id); // todo bu method qanday ishlaydi ??


    @Query("SELECT p FROM product p WHERE p.id = :id AND p.status IN :statuses")
    Optional<Product> findByIdAndStatus(@Param("id") Long id, @Param("statuses") List<Status> statuses);

    /// todo Bu metod @Query сиз йозиладими йокми текириш керак
    Page<Product> findByCreatedByAndStatus(User user, Status status, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM product p " +
            "LEFT JOIN FETCH p.rejectionReasons " +
            "LEFT JOIN FETCH p.productImages " +
            "WHERE p.createdBy = :user AND p.status = :status",
            countQuery = "SELECT count(p) FROM product p WHERE p.createdBy = :user AND p.status = :status")
    Page<Product> findUserProductsByStatus(
            @Param("user") User user,
            @Param("status") Status status,
            Pageable pageable
    );


    @Query("SELECT p FROM product p LEFT JOIN FETCH p.rejectionReasons WHERE p.id = :id")
    Optional<Product> findByIdWithRejectionReasons(@Param("id") Long id);
}