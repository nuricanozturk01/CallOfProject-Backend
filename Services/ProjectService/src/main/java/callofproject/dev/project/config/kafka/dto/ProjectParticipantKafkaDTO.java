package callofproject.dev.project.config.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProjectParticipantDTO
 */
public record ProjectParticipantKafkaDTO(
        UUID projectParticipantId,
        UUID projectId,
        UUID userId,
        LocalDateTime joinDate,
        boolean isDeleted)
{
    @Override
    public String toString()
    {
        return "ProjectParticipantDTO{" +
                "projectParticipantId=" + projectParticipantId +
                ", projectId=" + projectId +
                ", userId=" + userId +
                ", joinDate=" + joinDate +
                '}';
    }
}
