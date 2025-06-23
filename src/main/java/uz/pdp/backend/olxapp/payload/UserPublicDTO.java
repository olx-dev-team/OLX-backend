package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Avazbek on 23/06/25 22:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicDTO {

    private Long id;

    private String name;

}
