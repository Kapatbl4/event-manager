package vv.dev.notificator.notifications.model;

import vv.dev.eventcommon.kafka.ChangeItem;

import java.util.List;

public class EventPayload {

    private String eventName;
    private List<ChangeItem> changes;

    public EventPayload(String eventName, List<ChangeItem> changes) {
        this.eventName = eventName;
        this.changes = changes;
    }

    public EventPayload() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<ChangeItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeItem> changes) {
        this.changes = changes;
    }
}
