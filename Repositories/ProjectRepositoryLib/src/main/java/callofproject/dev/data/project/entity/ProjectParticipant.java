package callofproject.dev.data.project.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "project_participant")
public class ProjectParticipant
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_participant_id")
    private UUID m_projectId;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "project_id", nullable = false)
    private Project m_project;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "user_id", nullable = false)
    private User m_user;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime m_joinDate;

    @Column(name = "participant_rating")
    private double m_participantRating = 0; // Means no rating yet

    public ProjectParticipant()
    {
        m_joinDate = LocalDateTime.now();
    }


    public ProjectParticipant(Project project, User user)
    {
        m_project = project;
        m_user = user;
        m_joinDate = LocalDateTime.now();
    }

    public void setParticipantRating(double participantRating)
    {
        m_participantRating = participantRating;
    }

    public double getParticipantRating()
    {
        return m_participantRating;
    }

    public void rateParticipant(double rate)
    {
        m_participantRating = (m_participantRating + rate / 2) % 10;
    }

    public LocalDateTime getJoinDate()
    {
        return m_joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate)
    {
        m_joinDate = joinDate;
    }

    public UUID getProjectId()
    {
        return m_projectId;
    }

    public void setProjectId(UUID projectId)
    {
        m_projectId = projectId;
    }

    public Project getProject()
    {
        return m_project;
    }

    public void setProject(Project project)
    {
        m_project = project;
    }

    public User getUser()
    {
        return m_user;
    }

    public void setUser(User user)
    {
        m_user = user;
    }
}
