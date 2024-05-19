package callofproject.dev.data.community.entity;

import callofproject.dev.data.community.entity.enumeration.AdminOperationStatus;
import callofproject.dev.data.community.entity.enumeration.EProjectStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "project")
@SuppressWarnings("all")
public class Project
{
    @Id
    @Column(name = "project_id")
    private UUID m_projectId;

    @Column(name = "title", nullable = false, length = 100)
    private String m_projectName;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "user_id", nullable = false)
    private User m_projectOwner;

    @OneToMany(mappedBy = "m_project", cascade = {DETACH, MERGE, PERSIST, REFRESH}, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<ProjectParticipant> m_projectParticipants;
    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private EProjectStatus m_projectStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_operation_status")
    private AdminOperationStatus m_adminOperationStatus;


   /* @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "community_id", referencedColumnName = "community_id")
    private Community m_community;*/

    @Column(name = "created_at")
    private LocalDateTime m_deletedAt = null;



    public Project()
    {
    }

   /* public Project(UUID projectId, String projectName, User projectOwner, Community community)
    {
       // m_community = community;
        m_projectId = projectId;
        m_projectName = projectName;
        m_projectOwner = projectOwner;
    }*/

    public Project(UUID projectId, String projectName, User projectOwner)
    {
        m_projectId = projectId;
        m_projectName = projectName;
        m_projectOwner = projectOwner;
    }

    public Project(UUID projectId, String projectName, User projectOwner, Set<ProjectParticipant> projectParticipants,
                   EProjectStatus projectStatus, AdminOperationStatus adminOperationStatus)
    {
        m_projectId = projectId;
        m_projectName = projectName;
        m_projectOwner = projectOwner;
        m_projectParticipants = projectParticipants;
        m_projectStatus = projectStatus;
        m_adminOperationStatus = adminOperationStatus;
    }

    public LocalDateTime getDeletedAt()
    {
        return m_deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt)
    {
        m_deletedAt = deletedAt;
    }

  /*  public Community getCommunity()
    {
        return m_community;
    }

    public void setCommunity(Community community)
    {
        m_community = community;
    }*/

    public UUID getProjectId()
    {
        return m_projectId;
    }

    public void setProjectId(UUID projectId)
    {
        m_projectId = projectId;
    }

    public String getProjectName()
    {
        return m_projectName;
    }

    public void setProjectName(String projectName)
    {
        m_projectName = projectName;
    }

    public User getProjectOwner()
    {
        return m_projectOwner;
    }

    public void setProjectOwner(User projectOwner)
    {
        m_projectOwner = projectOwner;
    }

    public Set<ProjectParticipant> getProjectParticipants()
    {
        return m_projectParticipants;
    }

    public void setProjectParticipants(Set<ProjectParticipant> projectParticipants)
    {
        m_projectParticipants = projectParticipants;
    }

    public EProjectStatus getProjectStatus()
    {
        return m_projectStatus;
    }

    public void setProjectStatus(EProjectStatus projectStatus)
    {
        m_projectStatus = projectStatus;
    }

    public AdminOperationStatus getAdminOperationStatus()
    {
        return m_adminOperationStatus;
    }

    public void setAdminOperationStatus(AdminOperationStatus adminOperationStatus)
    {
        m_adminOperationStatus = adminOperationStatus;
    }
}