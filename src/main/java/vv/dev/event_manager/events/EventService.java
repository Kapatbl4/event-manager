package vv.dev.event_manager.events;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.events.model.dto.EventSearchRequestDto;
import vv.dev.event_manager.events.utils.EventMapper;
import vv.dev.event_manager.events.utils.PermissionService;
import vv.dev.event_manager.location.EventLocationRepository;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.UserRepository;
import vv.dev.event_manager.user.model.User;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventLocationRepository eventLocationRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final PermissionService permissionService;

    public EventService(
            EventRepository eventRepository,
            EventLocationRepository eventLocationRepository,
            UserRepository userRepository,
            EventMapper eventMapper,
            UserMapper userMapper,
            PermissionService permissionService
    ) {
        this.eventRepository = eventRepository;
        this.eventLocationRepository = eventLocationRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.permissionService = permissionService;
    }

    @Transactional
    public Event createEvent(Event event) {
        if (eventRepository.existsByName(event.getName())) {
            throw new IllegalArgumentException("Event name is already taken");
        }
        if (!eventLocationRepository.existsById(event.getLocation().getId())) {
            throw new IllegalArgumentException("Event location does not exists");
        }

        List<EventEntity> conflictEvents = this.findConflictingEvents(
                event.getLocation().getId(),
                event.getDate(),
                event.getDuration()
        );

        if (!conflictEvents.isEmpty()) {
            throw new IllegalArgumentException("Cannot create event: time conflict detected on this location");
        }

        if (event.getLocation().getCapacity() < event.getMaxPlaces()) {
            throw new IllegalArgumentException("Max places cannot be greater than capacity of location");
        }

        var savedEvent = eventRepository.save(eventMapper.fromDomainToEntity(event));

        return eventMapper.fromEntityToDomain(savedEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, Long userId) throws AccessDeniedException {
        var foundEvent = eventRepository.findById(eventId);
        if (foundEvent.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }
        Event event = eventMapper.fromEntityToDomain(foundEvent.get());
        User user = userMapper.fromEntityToDomain(
                userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );

        boolean isAdminOrOwner = permissionService.isAdminOrOwner(user, event);

        if (!isAdminOrOwner) {
            throw new AccessDeniedException("Access denied");
        }
        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalStateException("Cannot cancel event: you can cancel event with status WAIT_START only");
        }
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(eventMapper.fromDomainToEntity(event));
    }

    public Event getEventById(Long eventId) {
        var event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }
        return eventMapper.fromEntityToDomain(event.get());
    }

    @Transactional
    public Event updateEvent(Long eventId, Event data, Long userId) throws AccessDeniedException {
        var foundEvent = eventRepository.findById(eventId);
        if (foundEvent.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }
        Event event = eventMapper.fromEntityToDomain(foundEvent.get());
        User user = userMapper.fromEntityToDomain(
                userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );

        boolean isAdminOrOwner = permissionService.isAdminOrOwner(user, event);

        if (!isAdminOrOwner) {
            throw new AccessDeniedException("Access denied");
        }

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalStateException("You can update only waiting start event");
        }

        if (data.getMaxPlaces() < event.getOccupiedPlaces()) {
            throw new IllegalArgumentException("Cannot update event: capacity cannot be less than occupied places");
        }

        if (!eventLocationRepository.existsById(data.getLocation().getId())) {
            throw new IllegalArgumentException("Cannot update event: event location not found");
        }

        List<EventEntity> conflictEvents = new ArrayList<>();

        if (data.getDate() != null) {
            if (data.getDuration() != null) {
                conflictEvents = this.findConflictingEvents(
                        event.getLocation().getId(),
                        data.getDate(),
                        data.getDuration()
                );
            } else {
                conflictEvents = this.findConflictingEvents(
                        event.getLocation().getId(),
                        data.getDate(),
                        event.getDuration()
                );
            }
        } else {
            if (data.getDuration() != null) {
                conflictEvents = this.findConflictingEvents(
                        event.getLocation().getId(),
                        event.getDate(),
                        data.getDuration()
                );
            }
        }


        if (!conflictEvents.isEmpty() && !Objects.equals(eventId, event.getId())) {
            throw new IllegalArgumentException("Cannot update event: time conflict detected on this location");
        }

        if (data.getName() != null) {
            event.setName(data.getName());
        }
        if (data.getMaxPlaces() != null) {
            event.setMaxPlaces(data.getMaxPlaces());
        }
        if (data.getDate() != null) {
            event.setDate(data.getDate());
        }
        if (data.getCost() != null) {
            event.setCost(data.getCost());
        }
        if (data.getDuration() != null) {
            event.setDuration(data.getDuration());
        }
        if (data.getLocation() != null) {
            event.setLocation(data.getLocation());
        }

        var updatedEvent = eventRepository.save(eventMapper.fromDomainToEntity(event));

        return eventMapper.fromEntityToDomain(updatedEvent);
    }

    public List<Event> getCurrentUserEvents(User user) {
        List<EventEntity> foundEvents = eventRepository.findAllByUserId(user.getId());
        return foundEvents.stream()
                .map(eventMapper::fromEntityToDomain)
                .toList();
    }

    public Page<Event> searchEventsByParams(EventSearchRequestDto searchDto, Pageable pageable) {
        Specification<EventEntity> spec = Specification
                .where(EventSpecification.hasName(searchDto.getName()))
                .and(EventSpecification.hasLocationId(searchDto.getLocationId()))
                .and(EventSpecification.hasDurationMin(searchDto.getDurationMin()))
                .and(EventSpecification.hasDurationMax(searchDto.getDurationMax()))
                .and(EventSpecification.hasDateStartBefore(searchDto.getDateStartBefore()))
                .and(EventSpecification.hasDateStartAfter(searchDto.getDateStartAfter()))
                .and(EventSpecification.hasCostMin(searchDto.getCostMin()))
                .and(EventSpecification.hasCostMax(searchDto.getCostMax()))
                .and(EventSpecification.hasPlacesMin(searchDto.getPlacesMin()))
                .and(EventSpecification.hasPlacesMax(searchDto.getPlacesMax()))
                .and(EventSpecification.hasEventStatus(searchDto.getEventStatus()));

        Page<EventEntity> foundEventsPage = eventRepository.findAll(spec, pageable);
        return foundEventsPage
                .map(eventMapper::fromEntityToDomain);
    }

    private List<EventEntity> findConflictingEvents(
            Long locationId,
            LocalDateTime newEventStartTime,
            Integer newEventDuration
    ) {
        List<EventEntity> conflictedEvents = new ArrayList<>();
        LocalDateTime newEventEndTime = newEventStartTime.plusMinutes(newEventDuration);

        List<EventEntity> futureEvents = eventRepository.findEventsByLocationAndInTheFutureOrPresent(locationId);
        for (EventEntity event : futureEvents) {
            LocalDateTime endTime = event.getDate().plusMinutes(event.getDuration());
            if (newEventStartTime.isBefore(endTime) && event.getDate().isBefore(newEventEndTime)) {
                conflictedEvents.add(event);
            }
        }
        return conflictedEvents;
    }
}
