package vv.dev.event_manager.registration;

import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.user.model.User;

public class Registration {
    private Long id;
    private Event event;
    private User user;

    public Registration(Long id, Event event, User user) {
        this.id = id;
        this.event = event;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
