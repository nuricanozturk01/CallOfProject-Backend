package callofproject.dev.service.interview.dto.test;

public record CreateQuestionDTO(
        String question,
        String option1,
        String option2,
        String option3,
        String option4,
        String answer,
        int point)
{
}
