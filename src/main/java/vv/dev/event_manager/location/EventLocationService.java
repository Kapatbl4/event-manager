package vv.dev.event_manager.location;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vv.dev.event_manager.location.model.EventLocation;
import vv.dev.event_manager.location.model.EventLocationEntity;
import vv.dev.event_manager.location.model.dto.EventLocationFullUpdateDto;

import java.util.Optional;


@Service
public class EventLocationService {

    private final EventLocationRepository eventLocationRepository;
    private final EventLocationMapper eventLocationMapper;


    public EventLocationService(
            EventLocationRepository eventLocationRepository,
            EventLocationMapper eventLocationMapper
    ) {
        this.eventLocationRepository = eventLocationRepository;
        this.eventLocationMapper = eventLocationMapper;
    }

    @Transactional
    public EventLocation saveEventLocation(EventLocation eventLocation) {
        if (eventLocationRepository.existsByName(eventLocation.getName())) {
            throw new IllegalArgumentException("Event location name is already taken");
        }

        var entityToSave = eventLocationMapper.fromDomainToEntity(eventLocation);

        return eventLocationMapper.fromEntityToDomain(eventLocationRepository.save(entityToSave));
    }

    public Page<EventLocation> findAll(Pageable pageable) {
        return eventLocationRepository
                .findAll(pageable)
                .map(eventLocationMapper::fromEntityToDomain);
    }

    public EventLocation findById(Long locationId) {
        Optional<EventLocationEntity> entity = eventLocationRepository.findById(locationId);
        if (entity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }

        return eventLocationMapper.fromEntityToDomain(entity.get());
    }

    @Transactional
    public void deleteById(Long locationId) {
        if (!eventLocationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Entity not found");
        }
        eventLocationRepository.deleteById(locationId);
    }

    @Transactional
    public EventLocation fullUpdateById(
            Long locationId,
            @Valid EventLocationFullUpdateDto eventLocationFullUpdateDto
    ) {
        Optional<EventLocationEntity> foundEntity = eventLocationRepository.findById(locationId);
        if (foundEntity.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }

        EventLocationEntity entity = foundEntity.get();

        if(eventLocationFullUpdateDto.getCapacity() < entity.getCapacity()) {
            throw new IllegalArgumentException("Capacity cannot be changed to low");
        }

        eventLocationMapper.updateEntityFromDto(entity, eventLocationFullUpdateDto);

        return eventLocationMapper.fromEntityToDomain(eventLocationRepository.save(entity));
    }

}
