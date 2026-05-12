package vv.dev.event_manager.events.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vv.dev.event_manager.events.EventStatus;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.events.model.dto.EventCreateDto;
import vv.dev.event_manager.events.model.dto.EventDto;
import vv.dev.event_manager.events.model.dto.EventUpdateDto;
import vv.dev.event_manager.location.EventLocationMapper;
import vv.dev.event_manager.location.model.EventLocation;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.model.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventLocationMapper.class})
public interface EventMapper {

    default Event fromEventCreateDtoToDomain(
            EventCreateDto dto,
            User user,
            EventLocation eventLocation
            ) {
        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setCost(dto.getCost());
        event.setDuration(dto.getDuration());
        event.setMaxPlaces(dto.getMaxPlaces());
        event.setOccupiedPlaces(0);
        event.setStatus(EventStatus.WAIT_START);

        event.setOwner(user);
        event.setLocation(eventLocation);

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

    @Mapping(source = "locationId", target = "location.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "occupiedPlaces", ignore = true)
    @Mapping(target = "status", ignore = true)
    Event fromEventUpdateDtoToDomain(EventUpdateDto eventUpdateDto);

    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? String.valueOf(value) : null;
    }
}
