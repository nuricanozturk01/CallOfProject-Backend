package callofproject.dev.service.scheduler.dto;

import java.util.UUID;

/**
 * NotificationObject
 */
public record NotificationObject(
        UUID projectId,
        UUID userId
)
{
}
