package vv.dev.event_manager.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vv.dev.event_manager.location.model.EventLocationEntity;
import vv.dev.event_manager.location.model.dto.EventLocationFullUpdateDto;

@Mapper(componentModel = "spring")
public interface EventLocationMapper {

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(
            @MappingTarget EventLocationEntity entity,
            EventLocationFullUpdateDto eventLocationFullUpdateDto
    );
}
