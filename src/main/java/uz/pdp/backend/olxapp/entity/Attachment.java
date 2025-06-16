package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "attachment")
public class Attachment extends LongIdAbstract {

    @Column(columnDefinition = "text")
    private String originalName;

    private String contentType;

    private Long fileSize;

    @Column(columnDefinition = "text")
    private String path;
}
