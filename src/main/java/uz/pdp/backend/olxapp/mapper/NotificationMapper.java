package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.backend.olxapp.entity.Notification;
import uz.pdp.backend.olxapp.payload.NotificationDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "senderId",ignore = true)
    @Mapping(target = "receiverId",ignore = true)
    NotificationDTO toDto(Notification notification);

    @Mapping(target = "sender",ignore = true)
    @Mapping(target = "receiver",ignore = true)
    Notification toEntity(NotificationDTO notificationDTO);

    List<NotificationDTO> toDto(List<Notification> notifications);

}
