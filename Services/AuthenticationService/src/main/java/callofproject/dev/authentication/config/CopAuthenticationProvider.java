package callofproject.dev.authentication.config;

import callofproject.dev.library.exception.service.DataServiceException;
import callofproject.dev.repository.authentication.dal.UserManagementServiceHelper;
import callofproject.dev.repository.authentication.entity.Role;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is a Spring Component class that provides authentication for the application.
 */
@Component
public class CopAuthenticationProvider implements AuthenticationProvider
{
    private final UserManagementServiceHelper m_userManagementServiceHelper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new CopAuthenticationProvider.
     *
     * @param userManagementServiceHelper The UserManagementServiceHelper object to be injected.
     */
    public CopAuthenticationProvider(UserManagementServiceHelper userManagementServiceHelper, PasswordEncoder passwordEncoder)
    {
        m_userManagementServiceHelper = userManagementServiceHelper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Performs authentication with the same contract as AuthenticationManager.authenticate(Authentication).
     *
     * @param authentication the authentication request object.
     * @return Authentication
     * @throws AuthenticationException if the authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        var username = authentication.getName();
        var pwd = authentication.getCredentials().toString();
        var user = m_userManagementServiceHelper.getUserServiceHelper().findByUsername(username);

        if (user.isPresent())
        {
            if (passwordEncoder.matches(pwd, user.get().getPassword()))
                return new UsernamePasswordAuthenticationToken(username, pwd,
                        getGrantedAuthorities(user.get().getRoles()));
            else
                throw new DataServiceException("Invalid password!");
        } else throw new DataServiceException("No user registered with this details!");
    }

    /**
     * Returns a list of authorities granted to the user.
     *
     * @param authorities the authorities granted to the user.
     * @return List<GrantedAuthority>
     */
    private List<GrantedAuthority> getGrantedAuthorities(Set<Role> authorities)
    {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (var authority : authorities)
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));

        return grantedAuthorities;
    }

    /**
     * Returns true if this AuthenticationProvider supports the indicated Authentication object.
     *
     * @param authentication the authentication request object.
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> authentication)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}