package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.payload.ModeratedProductDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
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


}
