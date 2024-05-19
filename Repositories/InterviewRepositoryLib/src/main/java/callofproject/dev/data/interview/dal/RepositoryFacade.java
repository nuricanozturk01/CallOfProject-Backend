package callofproject.dev.data.interview.dal;

import callofproject.dev.data.interview.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class RepositoryFacade
{
    public final IUserRepository m_userRepository;
    public final IProjectRepository m_projectRepository;
    public final ITestInterviewRepository m_testInterviewRepository;
    public final ICodingInterviewRepository m_codingInterviewRepository;
    public final IProjectParticipantRepository m_projectParticipantRepository;
    public final ITestInterviewQuestionRepository m_testInterviewQuestionRepository;
    public final IUserCodingInterviewsRepository m_userCodingInterviewsRepository;
    public final IUserTestInterviewsRepository m_userTestInterviewsRepository;
    public final IQuestionAnswerRepository m_questionAnswerRepository;

    public RepositoryFacade(@Qualifier("callofproject.dev.data.interview.repository.IUserRepository") IUserRepository userRepository,
                            @Qualifier("callofproject.dev.data.interview.repository.IProjectRepository") IProjectRepository projectRepository,
                            @Qualifier("callofproject.dev.data.interview.repository.IProjectParticipantRepository") IProjectParticipantRepository projectParticipantRepository,
                            ITestInterviewRepository testInterviewRepository,
                            ITestInterviewQuestionRepository testInterviewQuestionRepository,
                            ICodingInterviewRepository codingInterviewRepository,
                            IUserCodingInterviewsRepository userCodingInterviewsRepository,
                            IUserTestInterviewsRepository userTestInterviewsRepository,
                            IQuestionAnswerRepository questionAnswerRepository)
    {
        m_userRepository = userRepository;
        m_projectRepository = projectRepository;
        m_projectParticipantRepository = projectParticipantRepository;
        m_testInterviewRepository = testInterviewRepository;
        m_testInterviewQuestionRepository = testInterviewQuestionRepository;
        m_codingInterviewRepository = codingInterviewRepository;
        m_userCodingInterviewsRepository = userCodingInterviewsRepository;
        m_userTestInterviewsRepository = userTestInterviewsRepository;
        m_questionAnswerRepository = questionAnswerRepository;
    }
}
