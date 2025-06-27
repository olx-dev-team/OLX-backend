package uz.pdp.backend.olxapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByCreatedBy(User createdBy, Pageable pageable);

    @Query("select p from product p where p.isApproved=false")
    Page<Product> findAllByIsApproved(Pageable pageable);
}