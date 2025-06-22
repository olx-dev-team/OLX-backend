package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);




}
