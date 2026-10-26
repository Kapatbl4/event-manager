package vv.dev.event_manager.kafka;

import vv.dev.eventcommon.kafka.ChangeItem;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EventChangeCollector {

    public List<ChangeItem> collectEventChanges(Event oldEvent, Event newEvent) {
        List<ChangeItem> changes = new ArrayList<>();

        addIfChanged(changes, "name",
                String.valueOf(oldEvent.getName()),
                String.valueOf(newEvent.getName())
        );

        addIfChanged(changes, "owner",
                String.valueOf(oldEvent.getOwner()),
                String.valueOf(newEvent.getOwner())
        );

        addIfChanged(changes, "maxPlaces",
                String.valueOf(oldEvent.getMaxPlaces()),
                String.valueOf(newEvent.getMaxPlaces())
        );

        addIfChanged(changes, "occupiedPlaces",
                String.valueOf(oldEvent.getOccupiedPlaces()),
                String.valueOf(newEvent.getOccupiedPlaces())
        );

        addIfChanged(changes, "date",
                String.valueOf(oldEvent.getDate()),
                String.valueOf(newEvent.getDate())
        );

        addIfChanged(changes, "cost",
                String.valueOf(oldEvent.getCost()),
                String.valueOf(newEvent.getCost())
        );

        addIfChanged(changes, "duration",
                String.valueOf(oldEvent.getDuration()),
                String.valueOf(newEvent.getDuration())
        );

        addIfChanged(changes, "location",
                String.valueOf(oldEvent.getLocation()),
                String.valueOf(newEvent.getLocation())
        );

        addIfChanged(changes, "status",
                String.valueOf(oldEvent.getStatus()),
                String.valueOf(newEvent.getStatus())
        );

        return changes;
    }

    private void addIfChanged(
            List<ChangeItem> changes,
            String field,
            String oldValue,
            String newValue
    ) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(new ChangeItem(
                    field,
                    oldValue,
                    newValue
            ));
        }
    }

}
