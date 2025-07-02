
package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.*;
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
     * Test successfully!
     *
     * @param page - defaultValue = "0"
     * @param size - defaultValue = "10"
     * @return page of products with userId == authUser.getId() and isActive == true
     */
    @GetMapping("/close/v1/my-products")
    public PageDTO<ProductDTO> getUserProductsIsApprovedTrue(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return productService.getMyProductsIsActive(page, size);
    }

    /**
     * Test successfully!
     *
     * @param page - defaultValue = "0"
     * @param size - defaultValue = "10"
     * @return page of products with status == WAITING
     */
    @GetMapping("/close/v1/products/waiting")
    public PageDTO<ProductDTO> getWaitingProducts(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return productService.getWaitingProducts(page, size);
    }

    /**
     * Test successfully!
     *
     * @param page - defaultValue = "0"
     * @param size - defaultValue = "10"
     * @return page of products with userId == authUser.getId() and isActive == false
     */
    @GetMapping("/close/v1/products/inactive")
    public PageDTO<ProductDTO> getInactiveProducts(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return productService.getInactiveProducts(page, size);
    }

    /**
     * Test successfully!
     *
     * @param page - defaultValue = "0"
     * @param size - defaultValue = "10"
     * @return page of products with userId == authUser.getId() and isActive == false and status == REJECTED
     */
    @GetMapping("/close/v1/products/rejected")
    public PageDTO<ProductDTO> getRejectedProducts(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return productService.getRejectedProducts(page, size);
    }

    /**
     * Test successfully!
     *
     * @param productReqDTO - DTO for creating a new product {
     *                         title: "String",
     *                         description: "String",
     *                         price: Double,
     *                         categoryId: Long,
     *                         imageDTOS: [
     *                             {
     *                                 file: File
     *                             },
     *                             ...
     *                         ]
     * }
     * @return created product
     */
    @PreAuthorize(value = "hasAnyRole('USER','ADMIN')")
    @PostMapping(value = "/close/v1/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createProduct(@ModelAttribute @Valid ProductReqDTO productReqDTO) {

        ProductDTO saveProduct = productService.save(productReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PutMapping(value = "/close/v1/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @ModelAttribute @Valid ProductUpdateDTO productUpdateDTO) {

        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedProduct);
    }

    /**
     * Test successfully!
     *
     * @param id - product id to change the status
     */
    @PreAuthorize(value = "hasRole('USER')")
    @PatchMapping("/close/v1/products/{id}/status")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable Long id) {

        productService.updateStatus(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Test successfully!
     *
     * @param id - product id to delete it from database
     */
    @PreAuthorize(value = "hasRole('USER')")
    @DeleteMapping("/close/v1/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok().build();

    }

    /**
     * Test successfully!
     *
     * @param filterDTO - DTO for filtering products by title, category and price range {
     *                    searchText: "String",
     *                    categoryId: Long,
     *                    minPrice: Double,
     *                    maxPrice: Double
     *                }
     * @return page of filtered products
     */
    @GetMapping("/open/v1/search")
    public ResponseEntity<PageDTO<ProductDTO>> searchProducts(
            ProductFilterDTO filterDTO,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(productService.searchProducts(filterDTO, page, size));
    }


}

