
package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductReqDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;
import uz.pdp.backend.olxapp.service.ProductService;

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

    /**
     * user ning approved == true va statusi active == true bo'lganlarini chiqaradi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/close/v1/my-products")
    public PageDTO<ProductDTO> getUserProductsIsApprovedTrue(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return productService.getUserProductsIsApprovedTrue(page, size);
    }

    @GetMapping("/close/v1/products/waiting")
    public PageDTO<ProductDTO> getWaitingProducts(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return productService.getWaitingProducts(page, size);
    }

    @GetMapping("/close/v1/products/inactive")
    public PageDTO<ProductDTO> getInactiveProducts(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return productService.getInactiveProducts(page, size);
    }

    @GetMapping("/close/v1/products/rejected")
    public PageDTO<ProductDTO> getRejectedProducts(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return productService.getRejectedProducts(page, size);
    }

    @PreAuthorize(value = "hasAnyRole('USER','ADMIN')")
    @PostMapping(value = "/close/v1/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createProduct(@ModelAttribute ProductReqDTO productReqDTO) {

        ProductDTO saveProduct = productService.save(productReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PutMapping(value = "/close/v1/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @ModelAttribute ProductUpdateDTO productUpdateDTO) {

        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO);
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
}

