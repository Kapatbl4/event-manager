package vv.dev.event_manager.location;

import org.springframework.stereotype.Component;
import vv.dev.event_manager.location.model.EventLocation;
import vv.dev.event_manager.location.model.EventLocationEntity;
import vv.dev.event_manager.location.model.dto.EventLocationCreateDto;
import vv.dev.event_manager.location.model.dto.EventLocationShowDto;

@Component
public class EventLocationConverter {
    public EventLocation fromCreateDtoToDomain(EventLocationCreateDto createDto) {
        return new EventLocation(
                createDto.getName(),
                createDto.getAddress(),
                createDto.getCapacity(),
                createDto.getDescription()
        );
    }

    public EventLocationEntity fromDomainToEntityForCreation(EventLocation eventLocation) {
        EventLocationEntity eventLocationEntity = new EventLocationEntity();
        eventLocationEntity.setName(eventLocation.getName());
        eventLocationEntity.setAddress(eventLocation.getAddress());
        eventLocationEntity.setCapacity(eventLocation.getCapacity());
        eventLocationEntity.setDescription(eventLocation.getDescription());
        return eventLocationEntity;
    }

    public EventLocationShowDto fromDomainToShowDto(EventLocation entity) {
        return new EventLocationShowDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCapacity(),
                entity.getDescription()
        );
    }

    public EventLocation fromEntityToDomain(EventLocationEntity entity) {
        return new EventLocation(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCapacity(),
                entity.getDescription()
        );
    }
}
