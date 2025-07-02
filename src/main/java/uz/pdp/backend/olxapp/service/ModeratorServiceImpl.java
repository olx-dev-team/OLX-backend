package uz.pdp.backend.olxapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.enums.Status;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.RejectionDTO;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by Avazbek on 26/06/25 13:54
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {

    private final ProductRepository productRepository;

    @Override
    public PageDTO<ModeratedProductDTO> getAll(Pageable pageable) {

        Page<Product> unapprovedProducts = productRepository.findAllByIsApproved(pageable, Status.PENDING_REVIEW);

        return new PageDTO<>(
                unapprovedProducts.getContent().stream().
                        map(product -> new ModeratedProductDTO(
                                        product.getId(),
                                        product.getTitle(),
                                        product.getDescription()
                                )
                        ).collect(Collectors.toList()),
                unapprovedProducts.getNumber(),
                unapprovedProducts.getSize(),
                unapprovedProducts.getTotalElements(),
                unapprovedProducts.getTotalPages(),
                unapprovedProducts.isLast(),
                unapprovedProducts.isFirst(),
                unapprovedProducts.getNumberOfElements(),
                unapprovedProducts.isEmpty()
        );
    }

    @Override
    @Transactional
    public ModeratedProductDTO approveProduct(Long productId) {
        log.info("Attempting to approve product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for approval: ID {}", productId);
                    return new EntityNotFoundException("Product not found with id: " + productId, HttpStatus.NOT_FOUND);
                });

        product.setStatus(Status.ACTIVE);
        productRepository.save(product);
        log.info("Product approved successfully: ID {}, Title '{}'", product.getId(), product.getTitle());

        return new ModeratedProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription()
        );
    }

    @Override
    @Transactional
    public ModeratedProductDTO rejectProduct(Long productId, RejectionDTO rejectionDTO) {
        log.info("Rejecting product ID {} with reasons: {}", productId, rejectionDTO.getReasons());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for rejection: ID {}", productId);
                    return new EntityNotFoundException("Product not found with id: " + productId, HttpStatus.NOT_FOUND);
                });

        product.setStatus(Status.REJECTED);
        product.setRejectionReasons(new HashSet<>(rejectionDTO.getReasons()));
        productRepository.save(product);

        log.info("Product rejected: ID {}, Title '{}', Reasons {}", product.getId(), product.getTitle(), product.getRejectionReasons());

        return new ModeratedProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription()
        );
    }
}
