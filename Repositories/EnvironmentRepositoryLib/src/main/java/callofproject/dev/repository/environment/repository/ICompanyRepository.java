package callofproject.dev.repository.environment.repository;

import callofproject.dev.repository.environment.entity.Company;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static callofproject.dev.repository.environment.BeanName.COMPANY_REPOSITORY;

@Repository(COMPANY_REPOSITORY)
@Lazy
public interface ICompanyRepository extends MongoRepository<Company, String>
{
    boolean existsByCompanyNameContainsIgnoreCase(String companyName);

    Optional<Company> findByCompanyNameIgnoreCase(String companyName);

    Iterable<Company> findAllByCompanyNameContainingIgnoreCase(String name);
}
