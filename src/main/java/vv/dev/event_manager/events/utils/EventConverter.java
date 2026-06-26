package vv.dev.event_manager.events.utils;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.events.model.dto.EventCreateDto;
import vv.dev.event_manager.events.model.dto.EventDto;
import vv.dev.event_manager.events.model.dto.EventUpdateDto;
import vv.dev.event_manager.location.EventLocationMapper;
import vv.dev.event_manager.location.EventLocationRepository;
import vv.dev.event_manager.location.model.EventLocation;
import vv.dev.event_manager.location.model.EventLocationEntity;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.UserRepository;
import vv.dev.event_manager.user.model.User;
import vv.dev.event_manager.user.model.UserEntity;

@Component
public class EventConverter {

    private final UserRepository userRepository;
    private final EventLocationRepository eventLocationRepository;
    private final EventMapper eventMapper;
    private final EventLocationMapper eventLocationMapper;
    private final UserMapper userMapper;


    public EventConverter(
            UserRepository userRepository,
            EventLocationRepository eventLocationRepository,
            EventMapper eventMapper,
            EventLocationMapper eventLocationMapper,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.eventLocationRepository = eventLocationRepository;
        this.eventMapper = eventMapper;
        this.eventLocationMapper = eventLocationMapper;
        this.userMapper = userMapper;
    }

    public Event fromEventCreateDtoToDomain(EventCreateDto dto, Long currentUserId) {
        UserEntity ownerEntity = userRepository
                .findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user = userMapper.fromEntityToDomain(ownerEntity);


        EventLocationEntity eventLocationEntity = eventLocationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        EventLocation eventLocation = eventLocationMapper.fromEntityToDomain(eventLocationEntity);

        return eventMapper.fromEventCreateDtoToDomain(
                dto,
                user,
                eventLocation
        );
    }

    public EventEntity fromDomainToEntity(Event event) {
        return eventMapper.fromDomainToEntity(event);
    }

    public Event fromEntityToDomain(EventEntity eventEntity) {
        return eventMapper.fromEntityToDomain(eventEntity);
    }

    public EventDto fromDomainToDto(Event event) {
        return eventMapper.fromDomainToDto(event);
    }

    public Event fromUpdateDtoToDomain(EventUpdateDto eventUpdateDto) {
        return eventMapper.fromEventUpdateDtoToDomain(eventUpdateDto);
    }

}
