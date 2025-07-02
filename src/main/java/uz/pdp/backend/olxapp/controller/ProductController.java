package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for managing products (CRUD, filtering, user-owned)")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products",
            description = "Returns a page of all approved and active products")
    @GetMapping("/open/v1/products")
    public PageDTO<ProductDTO> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        return productService.read(page, size);
    }

    @Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
    @GetMapping("/open/v1/products/{id}")
    public ProductDTO getProductById(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        return productService.read(id);
    }

    @Operation(summary = "Increase view count", description = "Increases view count of a product by its ID")
    @GetMapping("/open/v1/products/view/{id}")
    public ProductDTO increaseViewCount(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        return productService.increaseViewCount(id);
    }

    /**
     * Test successfully!
     *
     * @param page - defaultValue = "0"
     * @param size - defaultValue = "10"
     * @return page of products with userId == authUser.getId() and isActive == true
     */
    @Operation(summary = "Get my active products", description = "Returns all approved and active products of the authenticated user")
    @GetMapping("/close/v1/my-products")
    public PageDTO<ProductDTO> getUserProductsIsApprovedTrue(
            @RequestParam(defaultValue = "0") Integer page,
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
    @Operation(summary = "Get waiting products", description = "Returns products with status WAITING")
    @GetMapping("/close/v1/products/waiting")
    public PageDTO<ProductDTO> getWaitingProducts(
            @RequestParam(defaultValue = "0") Integer page,
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
    @Operation(summary = "Get inactive products", description = "Returns inactive products of authenticated user")
    @GetMapping("/close/v1/products/inactive")
    public PageDTO<ProductDTO> getInactiveProducts(
            @RequestParam(defaultValue = "0") Integer page,
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
    @Operation(summary = "Get rejected products", description = "Returns rejected products of authenticated user")
    @GetMapping("/close/v1/products/rejected")
    public PageDTO<ProductDTO> getRejectedProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return productService.getRejectedProducts(page, size);
    }

    /**
     * Test successfully!
     *
     * @param productReqDTO - DTO for creating a new product {
     *                      title: "String",
     *                      description: "String",
     *                      price: Double,
     *                      categoryId: Long,
     *                      imageDTOS: [
     *                      {
     *                      file: File
     *                      },
     *                      ...
     *                      ]
     *                      }
     * @return created product
     */
    @Operation(summary = "Create product", description = "Creates a new product with multipart form-data including optional images")
    @PreAuthorize(value = "hasAnyRole('USER','ADMIN')")
    @PostMapping(value = "/close/v1/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @ModelAttribute ProductReqDTO productReqDTO) {
        ProductDTO saveProduct = productService.save(productReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct);
    }

    @Operation(summary = "Update product", description = "Updates an existing product with new data and optional new image files")
    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PutMapping(value = "/close/v1/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductUpdateDTO productUpdateDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedProduct);
    }

    /**
     * Test successfully!
     *
     * @param id - product id to change the status
     */
    @Operation(summary = "Change product active status", description = "Toggles the active status of a product (e.g. deactivate listing)")
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
    @Operation(summary = "Delete product", description = "Deletes the product with the given ID")
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
     *                  searchText: "String",
     *                  categoryId: Long,
     *                  minPrice: Double,
     *                  maxPrice: Double
     *                  }
     * @return page of filtered products
     */
    @Operation(summary = "Search products", description = "Search and filter products by title, category and price range")
    @GetMapping("/open/v1/search")
    public ResponseEntity<PageDTO<ProductDTO>> searchProducts
    (
            @Parameter(description = "Product filtering options")
            ProductFilterDTO filterDTO,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(productService.searchProducts(filterDTO, page, size));
    }


    @GetMapping("/close/v1/product-moderation-status/{productId}")
    public ResponseEntity<ProductModerationStatusDTO> getProductModerationStatus
            (
                    @PathVariable Long productId,
                    @AuthenticationPrincipal User currentUser
            ) {
        ProductModerationStatusDTO moderationStatus = productService.getModerationStatus(productId, currentUser);

        return ResponseEntity.ok(moderationStatus);
    }


    @GetMapping("/rejected")
    public ResponseEntity<PageDTO<ProductModerationListDTO>> getRejectedProducts
            (
                    @AuthenticationPrincipal User currentUser
                    , @RequestParam(defaultValue = "0") Integer page
                    , @RequestParam(defaultValue = "10") Integer size
            ) {
        return ResponseEntity.ok(productService.findMyRejectedProduct(currentUser, page, size));
    }


}

