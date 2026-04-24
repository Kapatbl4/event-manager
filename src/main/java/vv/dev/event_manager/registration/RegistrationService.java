package vv.dev.event_manager.registration;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.events.EventRepository;
import vv.dev.event_manager.events.EventStatus;
import vv.dev.event_manager.events.utils.EventMapper;
import vv.dev.event_manager.registration.model.Registration;
import vv.dev.event_manager.registration.model.RegistrationEntity;
import vv.dev.event_manager.user.UserMapper;
import vv.dev.event_manager.user.model.User;

import java.util.List;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public RegistrationService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            UserMapper userMapper,
            EventMapper eventMapper
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public Registration register(User user, Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (event.getMaxPlaces() <= event.getOccupiedPlaces()) {
            throw new IllegalStateException("All places occupied");
        }
        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new IllegalArgumentException("This user is already registered for this event");
        }
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalStateException("You can register waiting start events only");
        }

        var registrationEntity = registrationRepository.save(
                new RegistrationEntity(null, event, userMapper.fromDomainToEntity(user))
        );
        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);

        return new Registration(
                registrationEntity.getId(),
                eventMapper.fromEntityToDomain(registrationEntity.getEvent()),
                userMapper.fromEntityToDomain(registrationEntity.getUser())
        );
    }

    @Transactional
    public void cancelRegistration(User user, Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalStateException("You can cancel register waiting start events only");
        }
        if (!registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new IllegalArgumentException("This user has not registration for this event");
        }
        var registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId());
        registrationRepository.delete(registration);

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    public List<Registration> getUserRegistrations(User user) {
        List<RegistrationEntity> foundEntities = registrationRepository.findAllByUserId(user.getId());
        return foundEntities.stream()
                .map(r -> new Registration(
                        r.getId(),
                        eventMapper.fromEntityToDomain(r.getEvent()),
                        userMapper.fromEntityToDomain(r.getUser())
                ))
                .toList();
    }
}
