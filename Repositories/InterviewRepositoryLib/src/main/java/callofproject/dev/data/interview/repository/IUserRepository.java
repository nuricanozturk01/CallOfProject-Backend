package callofproject.dev.data.interview.repository;



import callofproject.dev.data.interview.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("callofproject.dev.data.interview.repository.IUserRepository")
@Lazy
public interface IUserRepository extends CrudRepository<User, UUID>
{
    @Query("from User where m_username = :username")
    Optional<User> findByUsername(String username);
}
