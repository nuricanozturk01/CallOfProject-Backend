package callofproject.dev.project.dto;

import java.util.UUID;

/**
 * SaveProjectParticipantDTO
 */
public record SaveProjectParticipantDTO(UUID user_id, UUID project_id)
{
}
