package vv.dev.event_manager.events.model.dto;

import vv.dev.event_manager.events.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventSearchRequestDto {
    private String name;
    private Long locationId;
    private Integer durationMin;
    private Integer durationMax;
    private LocalDateTime dateStartBefore;
    private LocalDateTime dateStartAfter;
    private BigDecimal costMin;
    private BigDecimal costMax;
    private Integer placesMin;
    private Integer placesMax;
    private EventStatus eventStatus;

    public EventSearchRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public Integer getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Integer durationMax) {
        this.durationMax = durationMax;
    }

    public LocalDateTime getDateStartBefore() {
        return dateStartBefore;
    }

    public void setDateStartBefore(LocalDateTime dateStartBefore) {
        this.dateStartBefore = dateStartBefore;
    }

    public LocalDateTime getDateStartAfter() {
        return dateStartAfter;
    }

    public void setDateStartAfter(LocalDateTime dateStartAfter) {
        this.dateStartAfter = dateStartAfter;
    }

    public BigDecimal getCostMin() {
        return costMin;
    }

    public void setCostMin(BigDecimal costMin) {
        this.costMin = costMin;
    }

    public BigDecimal getCostMax() {
        return costMax;
    }

    public void setCostMax(BigDecimal costMax) {
        this.costMax = costMax;
    }

    public Integer getPlacesMin() {
        return placesMin;
    }

    public void setPlacesMin(Integer placesMin) {
        this.placesMin = placesMin;
    }

    public Integer getPlacesMax() {
        return placesMax;
    }

    public void setPlacesMax(Integer placesMax) {
        this.placesMax = placesMax;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
