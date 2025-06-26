package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilterDTO {

    private String search;

    private String title;

    private String description;

    private String categoryName;

    private BigDecimal fromPrice;

    private BigDecimal toPrice;

    private LocalDateTime fromCreatedAt;

    private LocalDateTime toCreatedAt;

    private LocalDateTime fromUpdatedAt;

    private LocalDateTime toUpdatedAt;
}
