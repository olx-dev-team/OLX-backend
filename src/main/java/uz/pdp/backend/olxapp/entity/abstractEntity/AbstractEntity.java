package uz.pdp.backend.olxapp.entity.abstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@MappedSuperclass
public abstract class AbstractEntity {

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(nullable = false)
    private boolean active = true;
}