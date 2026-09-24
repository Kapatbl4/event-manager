package vv.dev.notificator.notifications;

import java.util.List;

public class MarkAsReadRequest {
    private List<Long> notificationsIds;

    public MarkAsReadRequest(List<Long> notificationsIds) {
        this.notificationsIds = notificationsIds;
    }

    public List<Long> getNotificationsIds() {
        return notificationsIds;
    }

    public void setNotificationsIds(List<Long> notificationsIds) {
        this.notificationsIds = notificationsIds;
    }
}
