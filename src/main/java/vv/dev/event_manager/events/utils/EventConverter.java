package vv.dev.event_manager.events.utils;

import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.events.model.dto.EventCreateDto;
import vv.dev.event_manager.events.model.dto.EventDto;
import vv.dev.event_manager.events.model.dto.EventUpdateDto;
import vv.dev.event_manager.location.EventLocationMapper;
import vv.dev.event_manager.location.EventLocationRepository;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.UserRepository;

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
        return eventMapper.fromEventCreateDtoToDomain(
                dto,
                currentUserId,
                userRepository,
                eventLocationRepository,
                userMapper,
                eventLocationMapper
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
