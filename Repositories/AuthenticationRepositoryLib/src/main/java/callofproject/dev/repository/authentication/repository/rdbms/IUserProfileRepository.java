package callofproject.dev.repository.authentication.repository.rdbms;

import callofproject.dev.repository.authentication.entity.UserProfile;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static callofproject.dev.repository.authentication.BeanName.USER_PROFILE_REPOSITORY_BEAN;

@Repository(USER_PROFILE_REPOSITORY_BEAN)
@Lazy
public interface IUserProfileRepository extends CrudRepository<UserProfile, UUID>
{
    @Query("from UserProfile where user.userId = :userId")
    Optional<UserProfile> findUserProfileByUserId(@Param("userId") UUID userId);

    @Query("from UserProfile where user.username = :username")
    Optional<UserProfile> findUserProfileByUsername(@Param("username") String username);
}
