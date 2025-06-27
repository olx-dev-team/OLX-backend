package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductReqDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;

public interface ProductService {
    PageDTO<ProductDTO> read(Integer page, Integer size);

    ProductDTO read(Long id);

    ProductDTO increaseViewCount(Long id);

    ProductDTO save(ProductReqDTO productDTO);

    ProductDTO updateProduct(Long id, ProductUpdateDTO productDTO) ;

    void updateStatus(Long id, boolean active);

    void deleteProduct(Long id);

    PageDTO<ProductDTO> getUserProductsIsApprovedTrue(Integer page, Integer size);

    PageDTO<ProductDTO> getWaitingProducts(Integer page, Integer size);

    PageDTO<ProductDTO> getInactiveProducts(Integer page, Integer size);

    PageDTO<ProductDTO> getRejectedProducts(Integer page, Integer size);
}
