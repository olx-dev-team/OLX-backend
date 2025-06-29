package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.mapper.ProductMapper;
import uz.pdp.backend.olxapp.payload.FilterDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.repository.ChatRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

   /* private final ProductRepository productRepository;
    private final ChatRepository chatRepository;
    private final ProductMapper productMapper;

    @Override
    public PageDTO<ProductDTO> search(FilterDTO filterDTO, Integer page, Integer size) {

        Specification<Product> spec = ProductSpecifications.build(filterDTO);
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductDTO> content = productPage.getContent().stream()
                .map(productMapper::toDto)
                .toList();

        return new PageDTO<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast(),
                productPage.isFirst(),
                productPage.getNumberOfElements(),
                productPage.isEmpty()
        );
    }
*/

}
