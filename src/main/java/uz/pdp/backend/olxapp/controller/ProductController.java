package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product", description = "Endpoints for managing products including create, update, delete, view and approval")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Returns a paginated list of all products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved product list")
            }
    )
    @GetMapping("/open/v1/products")
    public PageDTO<ProductDTO> getAllProducts(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return productService.read(page, size);
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @GetMapping("/open/v1/products/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.read(id);
    }

    @Operation(
            summary = "Increase product view count",
            description = "Increments the view count of a product when accessed",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product view count incremented")
            }
    )
    @GetMapping("/open/v1/products/view/{id}")
    public ProductDTO increaseViewCount(@PathVariable Long id) {
        return productService.increaseViewCount(id);
    }

    @Operation(
            summary = "Create a new product",
            description = "Allows a user to create a new product entry including optional images",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully")
            }
    )
    @PreAuthorize(value = "hasRole('USER')")
    @PostMapping(value = "/close/v1/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") ProductReqDTO productReqDTO,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        ProductDTO saveProduct = productService.save(productReqDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct);
    }

    @Operation(
            summary = "Update product by ID",
            description = "Updates product information and associated images",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Product updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PreAuthorize(value = "hasRole('USER')")
    @PutMapping(value = "/close/v1/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") ProductUpdateDTO productUpdateDTO,
                                                    @RequestPart(name = "images") List<MultipartFile> images) {
        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO, images);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedProduct);
    }

    @Operation(
            summary = "Update product status",
            description = "Changes the active/inactive status of a product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProductReqDTO.class),
                            examples = @ExampleObject(
                                    name = "Update Status Example",
                                    value = "{ \"active\": true }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product status updated successfully")
            }
    )

    @PatchMapping("/close/v1/products/{id}/status")
    public ResponseEntity<Void> updateProductStatus(@PathVariable Long id,
                                                    @Valid @RequestBody ProductReqDTO dto) {
        productService.updateStatus(id, dto.isActive());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted successfully")
            }
    )
    @PreAuthorize(value = "hasRole('USER')")
    @DeleteMapping("/close/v1/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Approve a product (Admin only)",
            description = "Marks the product as approved by admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product approved successfully")
            }
    )
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PatchMapping("/close/v1/products/{id}/approve")
    public void approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
    }
}