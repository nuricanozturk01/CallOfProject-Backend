package callofproject.dev.project.config;

import callofproject.dev.service.jwt.filter.JWTTokenGeneratorFilter;
import callofproject.dev.service.jwt.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * SecurityConfig
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig
{
    /**
     * SecurityConfig
     */
    public SecurityConfig()
    {
    }

    /**
     * Customize security
     *
     * @param requests represent the request
     */
    private static void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests)
    {
        requests
                .requestMatchers(antMatcher("/api-docs/**")).permitAll()
                .requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(antMatcher("/api/project/admin/**")).hasAnyRole("ADMIN", "ROOT")
                .requestMatchers(antMatcher("/api/project/project-owner/**")).hasAnyRole("ADMIN", "USER", "ROOT")
                .requestMatchers(antMatcher("/api/project/project/**")).hasAnyRole("ROOT", "USER", "ADMIN")
                .requestMatchers(antMatcher("/api/project/storage/**")).hasAnyRole("ADMIN", "USER", "ROOT");
    }


    /**
     * Configure security
     *
     * @param http represent the http
     * @return SecurityFilterChain
     * @throws Exception if something goes wrong
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                //.cors(corsCustomizer -> corsCustomizer.configurationSource(this::setCorsConfig))
                .csrf(AbstractHttpConfigurer::disable);

        http.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(SecurityConfig::customize)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
                /*.exceptionHandling(ex -> ex.accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Access rejected: " + accessDeniedException.getMessage());
                }));*/

        return http.build();
    }
}