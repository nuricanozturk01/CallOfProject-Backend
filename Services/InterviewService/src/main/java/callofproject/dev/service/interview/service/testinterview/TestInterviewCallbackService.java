package callofproject.dev.service.interview.service.testinterview;

import callofproject.dev.data.common.clas.MultipleResponseMessage;
import callofproject.dev.data.common.clas.ResponseMessage;
import callofproject.dev.data.common.dsa.Pair;
import callofproject.dev.data.common.dto.EmailTopic;
import callofproject.dev.data.common.enums.EmailType;
import callofproject.dev.data.common.enums.NotificationType;
import callofproject.dev.data.common.status.Status;
import callofproject.dev.data.interview.dal.InterviewServiceHelper;
import callofproject.dev.data.interview.entity.*;
import callofproject.dev.data.interview.entity.enums.InterviewResult;
import callofproject.dev.data.interview.entity.enums.InterviewStatus;
import callofproject.dev.library.exception.service.DataServiceException;
import callofproject.dev.service.interview.config.kafka.KafkaProducer;
import callofproject.dev.service.interview.dto.InterviewResultDTO;
import callofproject.dev.service.interview.dto.NotificationKafkaDTO;
import callofproject.dev.service.interview.dto.UserEmailDTO;
import callofproject.dev.service.interview.dto.test.*;
import callofproject.dev.service.interview.mapper.IProjectMapper;
import callofproject.dev.service.interview.mapper.ITestInterviewMapper;
import callofproject.dev.service.interview.mapper.ITestInterviewQuestionMapper;
import callofproject.dev.service.interview.mapper.IUserMapper;
import callofproject.dev.service.interview.service.EInterviewStatus;
import callofproject.dev.service.interview.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.UUID.fromString;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Nuri Can ÖZTÜRK
 * The type Test interview callback service.
 * This class is used to handle the test interview service.
 */
@Service
@Lazy
@SuppressWarnings("all")
public class TestInterviewCallbackService
{
    private final InterviewServiceHelper m_interviewServiceHelper;
    private final ITestInterviewMapper m_testInterviewMapper;
    private final IProjectMapper m_projectMapper;
    private final IUserMapper m_userMapper;
    private final ITestInterviewQuestionMapper m_testInterviewQuestionMapper;
    @Value("${test-interview.email.template}")
    private String m_interviewEmail;
    private final KafkaProducer m_kafkaProducer;

    /**
     * Instantiates a new Test interview callback service.
     *
     * @param interviewServiceHelper      the interview service helper
     * @param testInterviewMapper         the test interview mapper
     * @param projectMapper               the project mapper
     * @param userMapper                  the user mapper
     * @param testInterviewQuestionMapper the test interview question mapper
     * @param kafkaProducer               the kafka producer
     */
    public TestInterviewCallbackService(InterviewServiceHelper interviewServiceHelper, ITestInterviewMapper testInterviewMapper, IProjectMapper projectMapper, IUserMapper userMapper, ITestInterviewQuestionMapper testInterviewQuestionMapper, KafkaProducer kafkaProducer)
    {
        m_interviewServiceHelper = interviewServiceHelper;
        m_testInterviewMapper = testInterviewMapper;
        m_projectMapper = projectMapper;
        m_userMapper = userMapper;
        m_testInterviewQuestionMapper = testInterviewQuestionMapper;
        m_kafkaProducer = kafkaProducer;
    }

    /**
     * Create a new test interview.
     *
     * @param dto the dto
     * @return the response message
     */
    public ResponseMessage<Object> createInterview(CreateTestDTO dto)
    {
        var project = findProjectIfExistsById(dto.projectId());
        var testInterview = m_interviewServiceHelper.createInterview(m_testInterviewMapper.toTestInterview(dto));

        if (testInterview.getStartTime().toLocalDate().equals(LocalDate.now()))
            testInterview.setInterviewStatus(InterviewStatus.STARTED);

        project.setTestInterview(testInterview);
        testInterview.setProject(project);
        m_interviewServiceHelper.createInterview(testInterview);
        m_interviewServiceHelper.createProject(project);

        // to question entity
        var questions = dto.questionList().stream().map(m_testInterviewQuestionMapper::toTestInterviewQuestion).toList();
        // Assign test interview to questions
        questions.forEach(q -> q.setTestInterview(testInterview));
        // Save questions
        var savedQuestions = stream(m_interviewServiceHelper.saveQuestions(questions).spliterator(), false).collect(toSet());
        //Assign questions to test interview
        testInterview.setQuestions(savedQuestions);
        // Save test interview
        var savedTestInterview = m_interviewServiceHelper.createInterview(testInterview);
        // Create User Test Interview
        var users = dto.userIds().stream().map(UUID::fromString).map(this::findUserIfExistsById).collect(toSet());
        users.stream().map(u -> new UserTestInterviews(u, savedTestInterview)).forEach(m_interviewServiceHelper::createUserTestInterviews);
        // Create DTO
        var savedTestInterviewDTO = m_testInterviewMapper.toTestInterviewDTO(savedTestInterview, m_projectMapper.toProjectDTO(savedTestInterview.getProject()));
        var userList = users.stream().map(m_userMapper::toUserEmailDTO).toList();

        return new ResponseMessage<>("Test interview created successfully", Status.CREATED, new Pair<>(savedTestInterviewDTO, userList));
    }


    /**
     * Delete test interview.
     *
     * @param interviewId the interview id
     * @return the response message
     */
    public ResponseMessage<Object> deleteTestInterview(UUID interviewId)
    {
        var interview = findInterviewIfExistsById(interviewId);

        var project = interview.getProject();
        var userTestInterviews = interview.getTestInterviews();
        var questions = interview.getQuestions();

        // Remove question answers
        userTestInterviews.forEach(uti -> {
            m_interviewServiceHelper.removeQuestionAnswers(uti.getAnswers().stream().toList());
        });

        // Clear test interviews from users
        userTestInterviews.forEach(uti -> {
            var user = uti.getUser();
            user.getTestInterviews().removeIf(testInterview -> testInterview.getTestInterview().getId().equals(interviewId));
            m_interviewServiceHelper.saveUser(user); // Assuming there's a method to update user in your service
        });

        // Remove user test interviews
        m_interviewServiceHelper.removeUserTestInterviews(userTestInterviews.stream().toList());

        // Remove test interview questions
        m_interviewServiceHelper.removeTestInterviewQuestions(questions.stream().toList());

        // Remove test interviews from project
        project.setTestInterview(null);
        interview.setProject(null);
        m_interviewServiceHelper.createProject(project);


        // Finally, delete the test interview itself
        m_interviewServiceHelper.deleteTestInterview(interview);

        var dto = m_testInterviewMapper.toTestInterviewDTO(interview, m_projectMapper.toProjectDTO(project));

        return new ResponseMessage<>("Test interview deleted successfully", Status.OK, dto);
    }


    /**
     * Delete test interview by project id response message.
     *
     * @param projectId the project id
     * @return the response message
     */
    public ResponseMessage<Object> deleteTestInterviewByProjectId(UUID projectId)
    {
        return deleteTestInterview(findProjectIfExistsById(projectId).getTestInterview().getId());
    }

    /**
     * Finish test interview.
     *
     * @param dto the TestInterviewFinishDTO
     * @return the response message
     */
    public ResponseMessage<Object> finishTestInterview(TestInterviewFinishDTO dto)
    {
        var interview = findInterviewIfExistsById(dto.interviewId());
        interview.setInterviewStatus(InterviewStatus.FINISHED);
        m_interviewServiceHelper.createInterview(interview);

        return new ResponseMessage<>("Test interview finished successfully", Status.OK, true);
    }

    /**
     * Start test interview response message.
     *
     * @param interviewId the interview id
     * @return the response message
     */
    public ResponseMessage<Object> startTestInterview(UUID interviewId)
    {
        var interview = findInterviewIfExistsById(interviewId);
        interview.setInterviewStatus(InterviewStatus.STARTED);
        m_interviewServiceHelper.createInterview(interview);

        return new ResponseMessage<>("Test interview started successfully", Status.OK, true);
    }

    /**
     * Start test interview by project id response message.
     *
     * @param projectId the project id
     * @return the response message
     */
    public ResponseMessage<Object> startTestInterviewByProjectId(UUID projectId)
    {
        var project = findProjectIfExistsById(projectId);

        return startTestInterview(project.getTestInterview().getId());
    }

    /**
     * Assign test interview to users.
     *
     * @param dto the dto
     * @return the response message
     */
    public ResponseMessage<Object> submitAnswer(QuestionAnswerDTO dto)
    {
        var interview = findInterviewIfExistsById(dto.interviewId());
        var user = findUserIfExistsById(dto.userId());

        var userTestInterview = m_interviewServiceHelper.findUserTestInterviewByUserAndTestInterviewId(user.getUserId(), interview.getId());
        var question = m_interviewServiceHelper.findQuestionById(dto.questionId());
        var answer = new QuestionAnswer(question.get().getId(), userTestInterview.get(), dto.answer());
        m_interviewServiceHelper.createQuestionAnswer(answer);

        return new ResponseMessage<>("Answer submitted successfully", Status.OK, true);
    }

    /**
     * Assign test interview to users.
     *
     * @param interviewId the interview id
     * @param q           question order
     * @return the response message
     */
    public ResponseMessage<Object> getQuestion(UUID interviewId, int q)
    {
        var interview = findInterviewIfExistsById(interviewId);

        if (q >= interview.getQuestions().size())
            return new ResponseMessage<>("Question not found", Status.NOT_FOUND, null);

        var question = interview.getQuestions().stream().sorted().toList().get(q);

        return new ResponseMessage<>("Question retrieved successfully", Status.OK, m_testInterviewQuestionMapper.toQuestionDTO(question));
    }


    /**
     * Get question response message.
     *
     * @param projectId the project id
     * @param q         the q
     * @return the response message
     */
    public ResponseMessage<Object> getQuestionByProjectId(UUID projectId, int q)
    {
        return getQuestion(findProjectIfExistsById(projectId).getTestInterview().getId(), q);
    }

    /**
     * Remove question from interview.
     *
     * @param questionId the question id
     * @return the response message
     */
    public ResponseMessage<Object> deleteQuestion(long questionId)
    {
        var question = findQuestionIfExistsById(questionId);

        m_interviewServiceHelper.deleteQuestion(question);

        return new ResponseMessage<>("Question deleted successfully", Status.OK, true);
    }


    /**
     * Get questions by interview id.
     *
     * @param interviewId the interview id
     * @return the multiple response message
     */
    public MultipleResponseMessage<Object> getQuestions(UUID interviewId)
    {
        var interview = findInterviewIfExistsById(interviewId);
        var questions = interview.getQuestions().stream().toList();

        return new MultipleResponseMessage<>(questions.size(), "Questions retrieved successfully", interview.getQuestions());
    }

    /**
     * Get questions by project id.
     *
     * @param projectId the project id
     * @return the multiple response message
     */
    public MultipleResponseMessage<Object> getQuestionsByProjectId(UUID projectId)
    {
        return getQuestions(findProjectIfExistsById(projectId).getTestInterview().getId());
    }

    /**
     * Get interview information response message.
     *
     * @param interviewId the interview id
     * @return the response message
     */
    public ResponseMessage<Object> getInterviewInformation(UUID interviewId)
    {
        var interview = findInterviewIfExistsById(interviewId);

        var dto = new TestInfoDTO(interview.getId(), interview.getQuestionCount(), interview.getTotalTimeMinutes());

        return new ResponseMessage<>("Interview information retrieved successfully", Status.OK, dto);
    }

    /**
     * Check if user solved before response message.
     *
     * @param interviewId the interview id
     * @param userId      the user id
     * @return the response message
     */
    public ResponseMessage<Object> isUserSolvedBefore(UUID userId, UUID interviewId)
    {
        var userTestInterview = m_interviewServiceHelper.findUserTestInterviewByUserAndTestInterviewId(userId, interviewId);

        if (userTestInterview.isEmpty())
            return new ResponseMessage<>("User not found", Status.NOT_FOUND, false);

        var interview = userTestInterview.get().getTestInterview();

        if (interview.getStartTime().isAfter(LocalDateTime.now()))
            return new ResponseMessage<>("Interview is not started yet", Status.NOT_ACCEPTED, false);

        if (interview.getEndTime().isBefore(LocalDateTime.now()))
            return new ResponseMessage<>("Interview is over", Status.NOT_ACCEPTED, false);

        var result = userTestInterview.get().getInterviewStatus() == InterviewStatus.COMPLETED;

        return new ResponseMessage<>("User solved before", Status.OK, result);
    }


    /**
     * Submit interview response message.
     *
     * @param testInterviewId the test interview id
     * @param userId          the user id
     * @return the response message
     */
    public ResponseMessage<Object> submitInterview(UUID testInterviewId, UUID userId)
    {
        var user = findUserIfExistsById(userId);
        var interview = findInterviewIfExistsById(testInterviewId);
        var userTestInterview = m_interviewServiceHelper.findUserTestInterviewByUserAndTestInterviewId(user.getUserId(), interview.getId());

        if (userTestInterview.isEmpty())
            throw new DataServiceException("User not assigned to interview");

        userTestInterview.get().setInterviewStatus(InterviewStatus.COMPLETED);
        userTestInterview.get().getAnswers().forEach(a -> a.setAnswer(a.getAnswer() == null ? "no_answer" : a.getAnswer()));
        var calculateScore = calculateScore(userTestInterview.get(), interview.getQuestions().stream().toList());
        userTestInterview.get().setScore(calculateScore);
        m_interviewServiceHelper.createUserTestInterviews(userTestInterview.get());

        return new ResponseMessage<>("Interview submitted successfully", Status.OK, true);
    }


    /**
     * Accept interview response message.
     *
     * @param id         the id
     * @param isAccepted the is accepted
     * @return the response message
     */
    public ResponseMessage<Object> acceptInterview(UUID id, boolean isAccepted)
    {
        var userTestInterview = m_interviewServiceHelper.findUserTestInterviewByInterviewId(id);

        if (userTestInterview.isEmpty())
            return new ResponseMessage<>("Interview not found", Status.NOT_FOUND, null);


        userTestInterview.get().setInterviewResult(isAccepted ? InterviewResult.PASSED : InterviewResult.FAILED);
        m_interviewServiceHelper.createUserTestInterviews(userTestInterview.get());

        var testInterview = userTestInterview.get().getTestInterview();
        var project = testInterview.getProject();
        var user = userTestInterview.get().getUser();
        var emailMsg = format("Hi %s! Your interview is %s!.", user.getUsername(), isAccepted ? "accepted" : "rejected");
        var dto = new InterviewResultDTO(project.getProjectOwner().getUserId(), user.getUserId(), project.getProjectName(), emailMsg, user.getEmail());
        var msg = format("Interview is %s!.", isAccepted ? "accepted" : "rejected");
        return new ResponseMessage<>(msg, Status.OK, dto);
    }

    /**
     * Find all test interviews response message.
     *
     * @return the multiple response message
     */
    public TestInterview findInterviewIfExistsById(UUID testInterviewId)
    {
        return m_interviewServiceHelper.findTestInterviewById(testInterviewId).orElseThrow(() -> new DataServiceException("Interview not found"));
    }

    /**
     * Send notification.
     *
     * @param object the object
     * @param status the status
     */
    public void sendNotification(TestInterviewDTO object, EInterviewStatus status)
    {
        var project = findProjectIfExistsById(object.projectDTO().projectId());


        var participants = project.getProjectParticipants().stream().map(ProjectParticipant::getUser).toList();
        var message = "A test interview has been %s for the Size %s Project application";

        switch (status)
        {
            case CREATED ->
                    participants.forEach(p -> send(project.getProjectOwner().getUserId(), p.getUserId(), format(message, "created", project.getProjectName())));
            case REMOVED ->
                    participants.forEach(p -> send(project.getProjectOwner().getUserId(), p.getUserId(), format(message, "removed", project.getProjectName())));
            case ASSIGNED ->
                    participants.forEach(p -> send(project.getProjectOwner().getUserId(), p.getUserId(), format(message, "assigned", project.getProjectName())));
            case CANCELLED ->
                    participants.forEach(p -> send(project.getProjectOwner().getUserId(), p.getUserId(), format(message, "cancelled", project.getProjectName())));
            default -> throw new DataServiceException("Invalid status");
        }
    }

    /**
     * Send emails.
     *
     * @param savedTestInterviewDTO the saved test interview dto
     * @param list                  the list
     * @param template              the template
     */
    public void sendEmails(TestInterviewDTO savedTestInterviewDTO, List<UserEmailDTO> list, String template)
    {
        list.forEach(u -> sendEmail(fromString(savedTestInterviewDTO.id()), u.email(), savedTestInterviewDTO.projectDTO().projectName(), u.userId(), template));
    }
    // ------------------------------private methods------------------------------

    private User findUserIfExistsById(UUID userId)
    {
        return m_interviewServiceHelper.findUserById(userId).orElseThrow(() -> new DataServiceException("User not found"));
    }

    private TestInterviewQuestion findQuestionIfExistsById(long questionId)
    {
        return m_interviewServiceHelper.findQuestionById(questionId).orElseThrow(() -> new DataServiceException("Question not found"));
    }

    private Project findProjectIfExistsById(UUID projectId)
    {
        return m_interviewServiceHelper.findProjectById(projectId).orElseThrow(() -> new DataServiceException("Project not found"));
    }

    private String toLocalDateTimeString(LocalDateTime localDateTime)
    {
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm:ss"));
    }

    private int calculateScore(UserTestInterviews userTestInterview, List<TestInterviewQuestion> questions)
    {
        var userAnswersMap = userTestInterview.getAnswers().stream()
                .collect(Collectors.toMap(QuestionAnswer::getQuestionId, QuestionAnswer::getAnswer));
        int score = 0;
        for (var question : questions)
        {
            var userAnswer = userAnswersMap.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getAnswer()))
                score += question.getPoint();
        }
        return score;
    }

    private void send(UUID owner, UUID userId, String message)
    {
        var notificationMessage = new NotificationKafkaDTO.Builder()
                .setFromUserId(owner)
                .setToUserId(userId)
                .setMessage(message)
                .setNotificationType(NotificationType.INFORMATION)
                .setNotificationLink("none")
                .setMessage(message)
                .setNotificationImage(null)
                .setNotificationTitle("Interview Status")
                .build();

        m_kafkaProducer.sendNotification(notificationMessage);
    }

    private void sendEmail(UUID interviewId, String email, String projectName, UUID userId, String templateName)
    {
        var template = Util.getEmailTemplate(templateName);
        var user = findUserIfExistsById(userId);
        var interview = findInterviewIfExistsById(interviewId);
        var startDate = toLocalDateTimeString(interview.getStartTime());
        var endDate = toLocalDateTimeString(interview.getEndTime());
        var emailStr = String.format(m_interviewEmail, interviewId, userId);
        var title = "Test Interview Assigned for " + projectName;
        var msg = format(template, projectName, user.getUsername(), interview.getTitle(), startDate, endDate, emailStr);

        var topic = new EmailTopic(EmailType.ASSIGN_INTERVIEW, email, title, msg, null);
        m_kafkaProducer.sendEmail(topic);
    }
}
