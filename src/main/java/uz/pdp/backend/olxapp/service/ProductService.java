package uz.pdp.backend.olxapp.service;

import org.springframework.data.domain.Pageable;
import uz.pdp.backend.olxapp.payload.*;

public interface ProductService {
    PageDTO<ProductDTO> read(Integer page, Integer size);

    ProductDTO read(Long id);

    ProductDTO increaseViewCount(Long id);

    ProductDTO save(ProductReqDTO productDTO);

    ProductDTO updateProduct(Long id, ProductUpdateDTO productDTO);

    void updateStatus(Long id);

    void deleteProduct(Long id);

    PageDTO<ProductDTO> getMyProductsIsActive(Integer page, Integer size);

    PageDTO<ProductDTO> getWaitingProducts(Integer page, Integer size);

    PageDTO<ProductDTO> getInactiveProducts(Integer page, Integer size);

    PageDTO<ProductDTO> getRejectedProducts(Integer page, Integer size);

    PageDTO<ProductDTO> searchProducts(ProductFilterDTO filterDTO, Integer page, Integer size);
}
