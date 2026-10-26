package vv.dev.notificator.notifications;

import vv.dev.notificator.notifications.model.UserEventNotificationDTO;
import vv.dev.notificator.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final JwtService jwtService;

    public NotificationController(
            NotificationService notificationService,
            JwtService jwtService
    ) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<UserEventNotificationDTO>> getUserNotifications(
            @RequestHeader(name = "Authorization") String jwt
    ) {
        log.info("Got request to get user notifications");

        Long userId = jwtService.extractUserId(jwt);
        List<UserEventNotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Void> markNotificationsAsRead(
            @RequestHeader(name = "Authorization") String jwt,
            @RequestBody MarkAsReadRequest readRequest
    ) {
        log.info("Got request to mark notifications as read");

        Long userId = jwtService.extractUserId(jwt);
        notificationService.markNotificationsAsRead(userId, readRequest.getNotificationsIds());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
