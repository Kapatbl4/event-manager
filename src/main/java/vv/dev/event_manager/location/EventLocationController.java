package vv.dev.event_manager.location;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vv.dev.event_manager.location.model.EventLocation;
import vv.dev.event_manager.location.model.dto.EventLocationCreateDto;
import vv.dev.event_manager.location.model.dto.EventLocationFullUpdateDto;
import vv.dev.event_manager.location.model.dto.EventLocationShowDto;


@RestController
@RequestMapping("/locations")
public class EventLocationController {
    private static final Logger log = LoggerFactory.getLogger(EventLocationController.class);

    private final EventLocationService service;

    private final EventLocationConverter eventLocationConverter;



    public EventLocationController(EventLocationService service, EventLocationConverter eventLocationConverter) {
        this.service = service;
        this.eventLocationConverter = eventLocationConverter;
    }

    @PostMapping
    public ResponseEntity<EventLocationShowDto> createLocation(
            @RequestBody @Valid EventLocationCreateDto eventLocationCreateDto
    ) {
        log.info("Got request for create Event location: eventLocation={}", eventLocationCreateDto);

        EventLocation createdEventLocation = service.saveEventLocation(
                eventLocationConverter.fromCreateDtoToDomain(eventLocationCreateDto)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventLocationConverter.fromDomainToShowDto(createdEventLocation));
    }

    @GetMapping
    public ResponseEntity<Page<EventLocationShowDto>> showAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Got request for show all Event locations");

        Pageable pageable = PageRequest.of(page, size);

        Page<EventLocation> locations = service.findAll(pageable);

        Page<EventLocationShowDto> dtos = locations.map(eventLocationConverter::fromDomainToShowDto);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<EventLocationShowDto> showById(@PathVariable Long locationId) {
        log.info("Got request for show Event location by id: locationId={}", locationId);

        return ResponseEntity.ok(eventLocationConverter.fromDomainToShowDto(service.findById(locationId)));
    }


    @PutMapping("/{locationId}")
    public ResponseEntity<EventLocationShowDto> fullUpdateById(
            @PathVariable Long locationId,
            @RequestBody @Valid EventLocationFullUpdateDto eventLocationFullUpdateDto
            ) {
        log.info("Got request for full update Event location by id: locationId={}", locationId);

        return ResponseEntity
                .ok(
                        eventLocationConverter.fromDomainToShowDto(
                                service.fullUpdateById(locationId, eventLocationFullUpdateDto)
                        )
                );
    }


    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long locationId) {
        log.info("Got request for delete Event location by id: locationId={}", locationId);

        service.deleteById(locationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
