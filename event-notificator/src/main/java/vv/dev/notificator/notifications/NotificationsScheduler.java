package vv.dev.notificator.notifications;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationsScheduler {

    private final NotificationService notificationService;

    public NotificationsScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${notifications.cleanup.cron:0 0 * * * *}")
    public void deleteOldNotifications() {
        notificationService.deleteOldNotifications();
    }
}
