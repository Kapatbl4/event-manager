package vv.dev.event_manager.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vv.dev.event_manager.events.utils.CurrentUserExtractor;
import vv.dev.event_manager.registration.model.Registration;
import vv.dev.event_manager.registration.model.RegistrationDto;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;
    private final CurrentUserExtractor currentUserExtractor;

    public RegistrationController(
            RegistrationService registrationService,
            CurrentUserExtractor currentUserExtractor
    ) {
        this.registrationService = registrationService;
        this.currentUserExtractor = currentUserExtractor;
    }

    @PostMapping("{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RegistrationDto> registerToEvent(
            @PathVariable Long eventId
    ) {
        log.info("Got request for registration to event with id: {}", eventId);

        var user = currentUserExtractor.getCurrentUser();
        var registration = registrationService.register(user, eventId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationDto(
                        registration.getId(),
                        registration.getEvent().getId(),
                        registration.getUser().getId()
                ));
    }

    @DeleteMapping("/cancel/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId) {
        log.info("Got request for cancel registration to event with id: {}", eventId);

        var user = currentUserExtractor.getCurrentUser();
        registrationService.cancelRegistration(user, eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<RegistrationDto>> getUserRegistrations() {
        log.info("Got request for getting all registrations of current user");

        var user = currentUserExtractor.getCurrentUser();
        List<Registration> registrations = registrationService.getUserRegistrations(user);
        return ResponseEntity.ok(
                registrations.stream()
                .map(r -> new RegistrationDto(
                        r.getId(),
                        r.getEvent().getId(),
                        r.getUser().getId()
                        ))
                .toList()
        );
    }
}
