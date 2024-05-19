package callofproject.dev.repository.authentication.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static callofproject.dev.repository.authentication.BeanName.*;

@Component(USER_MANAGEMENT_DAL_BEAN)
@Lazy
public class UserManagementServiceHelper
{
    private final UserServiceHelper m_userServiceHelper;
    private final UserProfileServiceHelper m_userProfileServiceHelper;
    private final EducationServiceHelper m_educationServiceHelper;
    private final CourseServiceHelper m_courseServiceHelper;
    private final LinkServiceHelper m_linkServiceHelper;
    private final ExperienceServiceHelper m_experienceServiceHelper;
    private final CourseOrganizationServiceHelper m_courseOrganizationServiceHelper;
    private final UserTagServiceHelper m_userTagServiceHelper;

    public UserManagementServiceHelper(@Qualifier(USER_DAL_BEAN) UserServiceHelper userServiceHelper,
                                       @Qualifier(USER_PROFILE_DAL_BEAN) UserProfileServiceHelper userProfileServiceHelper,
                                       @Qualifier(EDUCATION_DAL_BEAN) EducationServiceHelper educationServiceHelper,
                                       @Qualifier(COURSE_DAL_BEAN) CourseServiceHelper courseServiceHelper,
                                       @Qualifier(LINK_DAL_BEAN) LinkServiceHelper linkServiceHelper,
                                       @Qualifier(EXPERIENCE_DAL_BEAN) ExperienceServiceHelper experienceServiceHelper,
                                       @Qualifier(USER_TAG_DAL_BEAN) UserTagServiceHelper userTagServiceHelper,
                                       @Qualifier(COURSE_ORGANIZATION_DAL_BEAN) CourseOrganizationServiceHelper courseOrganizationServiceHelper)
    {
        m_userServiceHelper = userServiceHelper;
        m_userProfileServiceHelper = userProfileServiceHelper;
        m_educationServiceHelper = educationServiceHelper;
        m_courseServiceHelper = courseServiceHelper;
        m_linkServiceHelper = linkServiceHelper;
        m_experienceServiceHelper = experienceServiceHelper;
        m_courseOrganizationServiceHelper = courseOrganizationServiceHelper;
        m_userTagServiceHelper = userTagServiceHelper;
    }

    public CourseOrganizationServiceHelper getCourseOrganizationServiceHelper()
    {
        return m_courseOrganizationServiceHelper;
    }

    public UserServiceHelper getUserServiceHelper()
    {
        return m_userServiceHelper;
    }

    public UserProfileServiceHelper getUserProfileServiceHelper()
    {
        return m_userProfileServiceHelper;
    }

    public EducationServiceHelper getEducationServiceHelper()
    {
        return m_educationServiceHelper;
    }

    public CourseServiceHelper getCourseServiceHelper()
    {
        return m_courseServiceHelper;
    }

    public LinkServiceHelper getLinkServiceHelper()
    {
        return m_linkServiceHelper;
    }

    public ExperienceServiceHelper getExperienceServiceHelper()
    {
        return m_experienceServiceHelper;
    }

    public UserTagServiceHelper getUserTagServiceHelper()
    {
        return m_userTagServiceHelper;
    }
}
