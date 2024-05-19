package callofproject.dev.data.project.repository;

import callofproject.dev.data.project.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static callofproject.dev.data.project.ProjectRepositoryBeanName.USER_REPOSITORY;

@Repository(USER_REPOSITORY)
@Lazy
public interface IUserRepository extends CrudRepository<User, UUID>
{
    @Query("from User where m_username = :username")
    Optional<User> findByUsername(String username);
}
