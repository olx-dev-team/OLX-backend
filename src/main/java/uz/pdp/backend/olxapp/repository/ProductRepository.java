package uz.pdp.backend.olxapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.enums.Status;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByCreatedBy(User createdBy, Pageable pageable);

    Page<Product> findAllByIsApprovedTrueAndActiveTrue(Boolean isApproved, boolean active, Pageable pageable);

    default Page<Product> findAllByIsApprovedTrue(Pageable pageable) {
        return findAllByIsApprovedTrueAndActiveTrue(true, true, pageable);
    }

    Optional<Product> findByIdAndIsApprovedTrueAndActiveTrue(Long id);


    default Product findByIdAndIsApprovedTrue(Long id) {
        return findByIdAndIsApprovedTrueAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException("Product not found!", HttpStatus.NOT_FOUND));
    }

    Page<Product> findByCreatedByAndIsApprovedAndActive(User createdBy, Boolean isApproved, boolean active, Pageable pageable);


    Page<Product> findByCreatedByAndStatus(User user, Status status, Pageable pageable);
}