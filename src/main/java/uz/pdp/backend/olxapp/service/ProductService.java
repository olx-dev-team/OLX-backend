package uz.pdp.backend.olxapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductReqDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;

import java.util.List;

public interface ProductService {
    PageDTO<ProductDTO> read(Integer page, Integer size);

    ProductDTO read(Long id);

    ProductDTO increaseViewCount(Long id);

    ProductDTO save(ProductReqDTO productDTO, List<MultipartFile> images);

    ProductDTO updateProduct(Long id, ProductUpdateDTO productDTO, List<MultipartFile> images) ;

    void updateStatus(Long id, boolean active);

    void deleteProduct(Long id);

    void approveProduct(Long id);
}
