package callofproject.dev.authentication;

import callofproject.dev.authentication.config.kafka.KafkaProducer;
import callofproject.dev.authentication.dto.UserKafkaDTO;
import callofproject.dev.data.common.enums.EOperation;
import callofproject.dev.repository.authentication.entity.Role;
import callofproject.dev.repository.authentication.entity.User;
import callofproject.dev.repository.authentication.entity.UserProfile;
import callofproject.dev.repository.authentication.enumeration.RoleEnum;
import callofproject.dev.repository.authentication.repository.rdbms.IUserRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static callofproject.dev.authentication.util.AuthenticationServiceBeanName.BASE_PACKAGE;
import static callofproject.dev.authentication.util.AuthenticationServiceBeanName.REPO_PACKAGE;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {REPO_PACKAGE, BASE_PACKAGE, "callofproject.dev.service.jwt", "callofproject.dev.nosql"})
@EnableJpaRepositories(basePackages = REPO_PACKAGE) // Enable RDBMS ORM entities
@EnableMongoRepositories(basePackages = {REPO_PACKAGE, "callofproject.dev.nosql"}) // Enable NoSQL ORM entities
@EntityScan(basePackages = {REPO_PACKAGE, "callofproject.dev.nosql"})
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class AuthenticationServiceApplication implements ApplicationRunner
{
    private final IUserRepository m_userRepository;
    private final PasswordEncoder m_passwordEncoder;
    private final KafkaProducer m_kafkaProducer;
    @Value("${user.profile.default_pp}")
    private String m_defaultPp;

    public AuthenticationServiceApplication(IUserRepository userRepository, PasswordEncoder passwordEncoder, KafkaProducer kafkaProducer)
    {
        m_userRepository = userRepository;
        m_passwordEncoder = passwordEncoder;
        m_kafkaProducer = kafkaProducer;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args)
    {
        if (m_userRepository.findByUsername("cop_root").isEmpty())
        {
            var rootUser = new User("cop_root", "root", "root", "root", "nuricanozturk01@gmail.com",
                    m_passwordEncoder.encode("cop123"), LocalDate.now(), new Role(RoleEnum.ROLE_ROOT.getRole()));

            rootUser.addRoleToUser(new Role(RoleEnum.ROLE_USER.getRole()));
            rootUser.addRoleToUser(new Role(RoleEnum.ROLE_ADMIN.getRole()));
            var profile1 = new UserProfile();
            profile1.setProfilePhoto(m_defaultPp);
            profile1.setUser(rootUser);
            rootUser.setUserProfile(profile1);

            m_userRepository.save(rootUser);

            var adminUser = new User("cop_admin", "admin", "admin", "admin", "nuricanozturk02@gmail.com",
                    m_passwordEncoder.encode("cop123"), LocalDate.now(), new Role(RoleEnum.ROLE_ADMIN.getRole()));
            adminUser.setUserProfile(new UserProfile());
            adminUser.addRoleToUser(new Role(RoleEnum.ROLE_USER.getRole()));

            var profile2 = new UserProfile();
            profile2.setUser(adminUser);
            profile2.setProfilePhoto(m_defaultPp);
            adminUser.setUserProfile(profile2);
            m_userRepository.save(adminUser);

            m_kafkaProducer.sendMessage(new UserKafkaDTO(rootUser.getUserId(), rootUser.getUsername(), rootUser.getEmail(),
                    rootUser.getFirstName(), rootUser.getMiddleName(), rootUser.getLastName(), EOperation.CREATE, rootUser.getPassword(),
                    rootUser.getRoles(), null, 0, 0, 0, rootUser.getUserProfile().getProfilePhoto()));

            m_kafkaProducer.sendMessage(new UserKafkaDTO(adminUser.getUserId(), adminUser.getUsername(), adminUser.getEmail(),
                    adminUser.getFirstName(), adminUser.getMiddleName(), adminUser.getLastName(), EOperation.CREATE, adminUser.getPassword(),
                    adminUser.getRoles(), null, 0, 0, 0, adminUser.getUserProfile().getProfilePhoto()));
        }
    }
}
