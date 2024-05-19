package callofproject.dev.service.notification.controller;

import callofproject.dev.service.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static callofproject.dev.library.exception.util.ExceptionUtil.subscribe;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("api/notification")
@SecurityRequirement(name = "Authorization")
public class NotificationController
{
    private final NotificationService m_notificationService;

    /**
     * Constructor.
     *
     * @param notificationService represent the notification service.
     */
    public NotificationController(NotificationService notificationService)
    {
        m_notificationService = notificationService;
    }

    /**
     * Find all notifications by notification owner id and sort created at.
     *
     * @param userId represent the user id.
     * @param page   represent the page.
     * @return ResponseEntity.
     */
    @GetMapping("/find/all/sort/created-at")
    public ResponseEntity<Object> findAllNotificationsByNotificationOwnerIdAndSortCreatedAt(@RequestParam("uid") UUID userId, @RequestParam("p") int page)
    {
        return subscribe(() -> ok(m_notificationService.findAllNotificationsByNotificationOwnerIdAndSortCreatedAt(userId, page)),
                msg -> internalServerError().body(msg.getMessage()));
    }


    /**
     * Find unread notifications by notification owner id.
     *
     * @param userId represent the user id.
     * @return ResponseEntity.
     */
    @PostMapping("/mark/all/read")
    public ResponseEntity<Object> markAllNotificationRead(@RequestParam("uid") UUID userId)
    {
        return subscribe(() -> ok(m_notificationService.markAllNotificationRead(userId)),
                msg -> internalServerError().body(msg.getMessage()));
    }

    /**
     * Find all notifications by notification owner id and sort created at.
     *
     * @param userId represent the user id.
     * @return ResponseEntity.
     */
    @GetMapping("/find/all/count/unread")
    public ResponseEntity<Object> countUnreadNotificationsByNotificationOwnerId(@RequestParam("uid") UUID userId)
    {
        return subscribe(() -> ok(m_notificationService.countUnreadNotifications(userId)),
                msg -> internalServerError().body(msg.getMessage()));
    }

    /**
     * Clear all notifications by notification owner id.
     *
     * @param userId represent the user id.
     * @return ResponseEntity.
     */
    @DeleteMapping("/clear/all")
    public ResponseEntity<Object> clearAllNotificationsByNotificationOwnerId(@RequestParam("uid") UUID userId)
    {
        return subscribe(() -> ok(m_notificationService.clearAllNotificationsByNotificationOwnerId(userId)),
                msg -> internalServerError().body(msg.getMessage()));
    }
}
