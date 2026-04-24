package vv.dev.event_manager.events.utils;

import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.user.Role;
import vv.dev.event_manager.user.model.User;

@Component
public class PermissionService {
    public boolean isAdmin(User user) {
        return user.getRole().equals(Role.ADMIN);
    }

    public boolean isEventOwner(Event event, Long userId) {
        return event.getOwner().getId() == userId;
    }

    public boolean isAdminOrOwner(User user, Event event) {
        return user.getRole().equals(Role.ADMIN) || event.getOwner().getId() == user.getId();
    }
}
