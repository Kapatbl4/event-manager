package vv.dev.event_manager.user;

import org.mapstruct.Mapper;
import vv.dev.event_manager.user.model.User;
import vv.dev.event_manager.user.model.UserEntity;
import vv.dev.event_manager.user.model.dto.UserShowDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromEntityToDomain(UserEntity entity);

    UserEntity fromDomainToEntity(User user);

    UserShowDto fromDomainToDto(User user);
}
