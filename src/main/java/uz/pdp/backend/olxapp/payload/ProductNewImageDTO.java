package uz.pdp.backend.olxapp.payload;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductNewImageDTO {

    private MultipartFile file;
    private boolean main;

}
