package vv.dev.event_manager.kafka;

import VV.dev.eventcommon.kafka.ChangeItem;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EventChangeCollector {

    public List<ChangeItem> collectEventChanges(Event oldEvent, Event newEvent) {
        List<ChangeItem> changes = new ArrayList<>();

        if (!Objects.equals(oldEvent.getName(), newEvent.getName())) {
            changes.add(new ChangeItem(
                    "name",
                    String.valueOf(oldEvent.getName()),
                    String.valueOf(newEvent.getName())
            ));
        }

        if (!Objects.equals(oldEvent.getOwner().getId(), newEvent.getOwner().getId())) {
            changes.add(new ChangeItem(
                    "owner",
                    String.valueOf(oldEvent.getOwner().getId()),
                    String.valueOf(newEvent.getOwner().getId())
            ));
        }

        if (!Objects.equals(oldEvent.getMaxPlaces(), newEvent.getMaxPlaces())) {
            changes.add(new ChangeItem(
                    "maxPlaces",
                    String.valueOf(oldEvent.getMaxPlaces()),
                    String.valueOf(newEvent.getMaxPlaces())
            ));
        }

        if (!Objects.equals(oldEvent.getOccupiedPlaces(), newEvent.getOccupiedPlaces())) {
            changes.add(new ChangeItem(
                    "occupiedPlaces",
                    String.valueOf(oldEvent.getOccupiedPlaces()),
                    String.valueOf(newEvent.getOccupiedPlaces())
            ));
        }

        if (!Objects.equals(oldEvent.getDate(), newEvent.getDate())) {
            changes.add(new ChangeItem(
                    "date",
                    String.valueOf(oldEvent.getDate()),
                    String.valueOf(newEvent.getDate())
            ));
        }

        if (!Objects.equals(oldEvent.getCost(), newEvent.getCost())) {
            changes.add(new ChangeItem(
                    "cost",
                    String.valueOf(oldEvent.getCost()),
                    String.valueOf(newEvent.getCost())
            ));
        }

        if (!Objects.equals(oldEvent.getDuration(), newEvent.getDuration())) {
            changes.add(new ChangeItem(
                    "duration",
                    String.valueOf(oldEvent.getDuration()),
                    String.valueOf(newEvent.getDuration())
            ));
        }

        if (!Objects.equals(oldEvent.getLocation().getId(), newEvent.getLocation().getId())) {
            changes.add(new ChangeItem(
                    "location",
                    String.valueOf(oldEvent.getLocation().getId()),
                    String.valueOf(newEvent.getLocation().getId())
            ));
        }

        if (!Objects.equals(oldEvent.getStatus(), newEvent.getStatus())) {
            changes.add(new ChangeItem(
                    "status",
                    String.valueOf(oldEvent.getStatus()),
                    String.valueOf(newEvent.getStatus())
            ));
        }

        return changes;
    }

}
