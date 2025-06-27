package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.stream.Collectors;

/**
 * Created by Avazbek on 26/06/25 13:54
 */
@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {

    private final ProductRepository productRepository;

    @Override
    public PageDTO<ModeratedProductDTO> getAll(Pageable pageable) {

        Page<Product> unapprovedProducts = productRepository.findAllByIsApproved(pageable);


        return new PageDTO<>(
                unapprovedProducts.getContent().stream().
                        map(product -> new ModeratedProductDTO(
                                        product.getId(),
                                        product.getTitle(),
                                        product.getDescription(),
                                        product.getIsApproved()
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
}
