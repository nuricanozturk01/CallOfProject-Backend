package callofproject.dev.task.dto.request;

import callofproject.dev.data.task.entity.enums.Priority;
import callofproject.dev.data.task.entity.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Task Filter DTO
 * <p>
 * This class contains the task filter data transfer object
 */
public record TaskFilterDTO(
        @JsonProperty(value = "priority")
        Priority priority,
        @JsonProperty(value = "task_status")
        TaskStatus taskStatus,
        @JsonProperty(value = "start_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate startDate,
        @JsonProperty(value = "end_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate finishDate,
        @JsonProperty(value = "project_id")
        @NotNull(message = "project id cannot be null")
        UUID projectId,
        @JsonProperty(value = "project_owner_id")
        UUID projectOwnerId)
{
}
