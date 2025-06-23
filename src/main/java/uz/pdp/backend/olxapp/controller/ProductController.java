package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductReqDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;
import uz.pdp.backend.olxapp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/open/v1/products")
    public PageDTO<ProductDTO> getAllProducts(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return productService.read(page, size);
    }

    @GetMapping("/open/v1/products/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.read(id);
    }

    @GetMapping("/open/v1/products/view/{id}")
    public ProductDTO increaseViewCount(@PathVariable Long id) {
        return productService.increaseViewCount(id);
    }

    @PreAuthorize(value = "hasRole('USER')")
    @PostMapping(value = "/close/v1/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createProduct(@RequestPart("product") ProductReqDTO productReqDTO,
                                                    @RequestPart(name = "images", required = false) List<MultipartFile> images) {

        ProductDTO saveProduct = productService.save(productReqDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct);
    }

    @PreAuthorize(value = "hasRole('USER')")
    @PutMapping(value = "/close/v1/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") ProductUpdateDTO productUpdateDTO,
                                                    @RequestPart(name = "images") List<MultipartFile> images) {

        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO, images);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedProduct);
    }

    @PreAuthorize(value = "hasRole('USER')")
    @PatchMapping("/close/v1/products/{id}/status")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductReqDTO dto) {

        productService.updateStatus(id, dto.isActive());

        // Muvaffaqiyatli o'zgarganda, odatda bo'sh javob (204 No Content)
        // yoki yangilangan obyektni qaytarish mumkin. Bo'sh javob samaraliroq.
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(value = "hasRole('USER')")
    @DeleteMapping("/close/v1/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok().build();

    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @PatchMapping("/close/v1/products/{id}/approve")
    public void approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
    }


}
