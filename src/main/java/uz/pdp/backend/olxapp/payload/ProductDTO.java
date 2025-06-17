package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Product}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean active;

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private Boolean isApproved = false;

    private Integer viewCounter = 0;

    private Long categoryId;

    private List<Favorites> favorites = new ArrayList<>();

    private List<Attachment> attachments = new ArrayList<>();

    private Long createdById;

}