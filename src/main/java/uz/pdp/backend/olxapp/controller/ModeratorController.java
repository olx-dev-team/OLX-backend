package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.RejectionDTO;
import uz.pdp.backend.olxapp.service.ModeratorService;

/**
 * Created by Avazbek on 26/06/25 14:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moderator")
@PreAuthorize("hasAnyRole('ROLE_MODERATOR' , 'ROLE_ADMIN')")
public class ModeratorController {

    private final ModeratorService moderatorService;


    @GetMapping("/products")
    public ResponseEntity<PageDTO<ModeratedProductDTO>> getAll(Pageable pageable) {

        PageDTO<ModeratedProductDTO> products = moderatorService.getAll(pageable);

        return ResponseEntity.ok(products);
    }

    @PostMapping("/{productId}/approve")
    public ResponseEntity<ModeratedProductDTO> approveProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(moderatorService.approveProduct(productId));
    }


    @PostMapping("/{productId}/reject")
    public ResponseEntity<ModeratedProductDTO> rejectProduct(@PathVariable Long productId,
                                                             @Valid @RequestBody RejectionDTO rejectionDTO) {
        return ResponseEntity.ok(moderatorService.rejectProduct(productId, rejectionDTO));
    }


}
