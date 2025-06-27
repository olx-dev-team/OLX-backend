package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.enums.Role;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeUserRole {

    private Role role;
    private Boolean active;
}
