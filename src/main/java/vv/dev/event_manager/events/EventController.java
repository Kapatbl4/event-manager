package vv.dev.event_manager.events;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.dto.EventCreateDto;
import vv.dev.event_manager.events.model.dto.EventDto;
import vv.dev.event_manager.events.model.dto.EventSearchRequestDto;
import vv.dev.event_manager.events.model.dto.EventUpdateDto;
import vv.dev.event_manager.events.utils.CurrentUserExtractor;
import vv.dev.event_manager.events.utils.EventConverter;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final EventConverter eventConverter;
    public final CurrentUserExtractor currentUserExtractor;

    public EventController(
            EventService eventService,
            EventConverter eventConverter,
            CurrentUserExtractor currentUserExtractor
    ) {
        this.eventService = eventService;
        this.eventConverter = eventConverter;
        this.currentUserExtractor = currentUserExtractor;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventCreateDto eventCreateDto) {
        log.info("Got request to create event: event name={}", eventCreateDto.getName());

        var user = currentUserExtractor.getCurrentUser();

        var createdEvent = eventService.createEvent(
                eventConverter.fromEventCreateDtoToDomain(eventCreateDto, user.getId())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(eventConverter.fromDomainToDto(createdEvent));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable(name = "eventId") Long eventId) throws AccessDeniedException {
        log.info("Got request to delete event: event id={}", eventId);

        var user = currentUserExtractor.getCurrentUser();

        eventService.deleteEvent(eventId, user.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<EventDto> getEventById(@PathVariable(name = "eventId") Long eventId) {
        log.info("Got request for get event by id: event id={}", eventId);

        var event = eventService.getEventById(eventId);
        return ResponseEntity.ok(eventConverter.fromDomainToDto(event));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable(name = "eventId") Long eventId,
            @RequestBody @Valid EventUpdateDto data
            )
            throws AccessDeniedException
    {
        log.info("Got request to update event: event id={}", eventId);

        var user = currentUserExtractor.getCurrentUser();

        var updatedEvent = eventService.updateEvent(
                eventId,
                eventConverter.fromUpdateDtoToDomain(data),
                user.getId()
        );

        return ResponseEntity.ok(eventConverter.fromDomainToDto(updatedEvent));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getCurrentUserEvents() {
        log.info("Got request to get all current user events");

        var user = currentUserExtractor.getCurrentUser();

        List<Event> events = eventService.getCurrentUserEvents(user);

        List<EventDto> result = events.stream()
                .map(eventConverter::fromDomainToDto)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Page<EventDto>> searchEvents(
            @RequestBody EventSearchRequestDto searchDto,
            Pageable pageable
            ) {
        Page<Event> foundedEvents = eventService.searchEventsByParams(searchDto, pageable);
        Page<EventDto> result = foundedEvents.map(eventConverter::fromDomainToDto);
        return ResponseEntity.ok(result);
    }

}
