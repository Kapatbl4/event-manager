package vv.dev.event_manager.events;

import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.events.model.dto.EventCreateDto;
import vv.dev.event_manager.events.model.dto.EventDto;
import vv.dev.event_manager.location.EventLocationMapper;
import vv.dev.event_manager.location.EventLocationRepository;
import vv.dev.event_manager.location.model.EventLocationEntity;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.UserRepository;
import vv.dev.event_manager.user.model.UserEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventLocationMapper.class})
public interface EventMapper {

    default Event fromEventCreateDtoToDomain(
            EventCreateDto dto,
            Long currentUserId,
            @Context UserRepository userRepository,
            @Context EventLocationRepository eventLocationRepository,
            @Context UserMapper userMapper,
            @Context EventLocationMapper eventLocationMapper
            ) {
        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setCost(dto.getCost());
        event.setDuration(dto.getDuration());
        event.setMaxPlaces(dto.getMaxPlaces());
        event.setOccupiedPlaces(0);
        event.setStatus(EventStatus.WAIT_START);

        UserEntity ownerEntity = userRepository
                .findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        event.setOwner(userMapper.fromEntityToDomain(ownerEntity));

        if (dto.getLocationId() != null) {
            EventLocationEntity eventLocationEntity = eventLocationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            event.setLocation(eventLocationMapper.fromEntityToDomain(eventLocationEntity));
        }
        return event;
    }

    @Mapping(source = "owner.id", target = "owner.id")
    @Mapping(source = "location.id", target = "location.id")
    EventEntity fromDomainToEntity(Event event);

    @Mapping(source = "owner.id", target = "owner.id")
    @Mapping(source = "location.id", target = "location.id")
    Event fromEntityToDomain(EventEntity eventEntity);

    @Mapping(source = "owner.id", target = "ownerId", qualifiedByName = "longToString")
    @Mapping(source = "location.id", target = "locationId")
    EventDto fromDomainToDto(Event event);

    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? String.valueOf(value) : null;
    }
}
