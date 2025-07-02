package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.RejectionDTO;
import uz.pdp.backend.olxapp.service.ModeratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Created by Avazbek on 26/06/25 14:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/close/moderator")
@PreAuthorize("hasAnyRole('ROLE_MODERATOR' , 'ROLE_ADMIN')")
@Tag(name = "Moderator", description = "APIs for product moderation (approval and rejection)")
public class ModeratorController {

    private final ModeratorService moderatorService;


    /**
     * Test successfully
     *
     * @param page - default 0
     * @param size - default 10
     * @return all products that are waiting for moderation
     */
    @Operation(
            summary = "Get all products awaiting moderation",
            description = "Returns a paginated list of all products that are waiting for moderation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of products successfully returned")
            }
    )
    @GetMapping("/products")
    public ResponseEntity<PageDTO<ModeratedProductDTO>> getAll(
            @Parameter(description = "Page number (starts from 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, LongIdAbstract.Fields.id);
        PageRequest pageable = PageRequest.of(page, size, sort);
        PageDTO<ModeratedProductDTO> products = moderatorService.getAll(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Test successfully
     *
     * @param productId - with product id
     * @return approved product
     */
    @Operation(
            summary = "Approve product",
            description = "Approve the product with given ID so it becomes visible to users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product successfully approved"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PostMapping("/{productId}/approve")
    public ResponseEntity<ModeratedProductDTO> approveProduct(
            @Parameter(description = "ID of the product to approve", example = "123")
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(moderatorService.approveProduct(productId));
    }


    /**
     * Test successfully
     *
     * @param productId    - with product id
     * @param rejectionDTO - with rejection reasons { "INAPPROPRIATE_TITLE", "FORBIDDEN_CONTENT" ... }
     * @return rejected product and rejection reasons
     */
    @Operation(
            summary = "Reject product",
            description = "Reject the product with given ID and provide reasons for the rejection",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of rejection reasons",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RejectionDTO.class),
                            examples = @ExampleObject(
                                    name = "Rejection Example",
                                    value = """
                                            {
                                                "reasons": [
                                                    "INAPPROPRIATE_TITLE",
                                                    "FORBIDDEN_CONTENT"
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product successfully rejected"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PostMapping("/{productId}/reject")
    public ResponseEntity<ModeratedProductDTO> rejectProduct(
            @Parameter(description = "ID of the product to reject", example = "123")
            @PathVariable Long productId,
            @Valid @RequestBody RejectionDTO rejectionDTO
    ) {
        return ResponseEntity.ok(moderatorService.rejectProduct(productId, rejectionDTO));
    }
}
