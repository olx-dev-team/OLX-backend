package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.Entity;
import lombok.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Permission;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "roles")
public class Role extends LongIdAbstract {

    private String name;

    private List<Permission> permissions;
}