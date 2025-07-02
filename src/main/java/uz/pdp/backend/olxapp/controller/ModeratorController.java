package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.RejectionDTO;
import uz.pdp.backend.olxapp.service.ModeratorService;

/**
 * Created by Avazbek on 26/06/25 14:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/close/moderator")
@PreAuthorize("hasAnyRole('ROLE_MODERATOR' , 'ROLE_ADMIN')")
public class ModeratorController {

    private final ModeratorService moderatorService;


    /**
     * Test successfully
     *
     * @param page - default 0
     * @param size - default 10
     * @return all products that are waiting for moderation
     */
    @GetMapping("/products")
    public ResponseEntity<PageDTO<ModeratedProductDTO>> getAll(
            @RequestParam(defaultValue = "0") Integer page,
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
    @PostMapping("/{productId}/approve")
    public ResponseEntity<ModeratedProductDTO> approveProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(moderatorService.approveProduct(productId));
    }


    /**
     * Test successfully
     *
     * @param productId    - with product id
     * @param rejectionDTO - with rejection reasons { "INAPPROPRIATE_TITLE", "FORBIDDEN_CONTENT" ... }
     * @return rejected product and rejection reasons
     */
    @PostMapping("/{productId}/reject")
    public ResponseEntity<ModeratedProductDTO> rejectProduct(@PathVariable Long productId,
                                                             @Valid @RequestBody RejectionDTO rejectionDTO) {
        return ResponseEntity.ok(moderatorService.rejectProduct(productId, rejectionDTO));
    }


}
