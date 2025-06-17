package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.enums.Permission;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Role}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO implements Serializable {
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean active;
    private Long id;
    private String name;
    private List<Permission> permissions;
}