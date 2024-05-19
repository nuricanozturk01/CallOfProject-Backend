package callofproject.dev.authentication.controller;


import callofproject.dev.authentication.dto.auth.AuthenticationRequest;
import callofproject.dev.authentication.dto.auth.RegisterRequest;
import callofproject.dev.authentication.service.authentication.AuthenticationService;
import callofproject.dev.authentication.util.AuthenticationServiceBeanName;
import callofproject.dev.data.common.clas.ErrorMessage;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static callofproject.dev.library.exception.util.ExceptionUtil.subscribe;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Authentication Controller
 * Copyleft (C), 2023, Cal-of-Project-Teams Developers.
 * All Rights free.
 */
@RestController
@RequestMapping("api/auth/authenticate")
@SecurityRequirement(name = "Authorization")
public class AuthenticationController
{
    private final AuthenticationService m_authenticationService;


    /**
     * Constructor for the AuthenticationController class.
     * It is used to inject dependencies into the controller.
     *
     * @param service The AuthenticationService object to be injected.
     */
    public AuthenticationController(@Qualifier(AuthenticationServiceBeanName.AUTHENTICATION_SERVICE) AuthenticationService service)
    {
        this.m_authenticationService = service;
    }


    /**
     * Find user by username.
     *
     * @param username represent the username.
     * @return if success returns User else return ErrorMessage.
     */
    @GetMapping("/find/username")
    public ResponseEntity<Object> findUserByUsername(@RequestParam("username") String username)
    {
        return subscribe(() -> ok(m_authenticationService.findUserByUsername(username)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Register to application.
     *
     * @param request represent the Register information.
     * @return if success AuthenticationResponse else return ErrorMessage.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request)
    {
        return subscribe(() -> ok(m_authenticationService.register(request)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }


    /**
     * Register to application.
     *
     * @param request represent the Register information.
     * @return if success AuthenticationResponse else return ErrorMessage.
     */
    @PostMapping("/register/mobile") //TODO: Future, this will be removed and replaced with OTP
    public ResponseEntity<Object> registerForMobile(@Valid @RequestBody RegisterRequest request)
    {
        return subscribe(() -> ok(m_authenticationService.registerForMobile(request)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Verify the user and register to application.
     *
     * @param token represent the token that sent to user.
     * @return if success AuthenticationResponse else return ErrorMessage.
     */
    @GetMapping("/register/verify")
    public ResponseEntity<Object> verify(@RequestParam("token") String token)
    {
        return subscribe(() -> ok(m_authenticationService.verifyUserAndRegister(token)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Login operation for application.
     *
     * @param request represent the login information.
     * @return if success returns AuthenticationResponse that include token and status else return ErrorMessage.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@RequestBody @Valid AuthenticationRequest request)
    {
        return subscribe(() -> ok(m_authenticationService.authenticate(request)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Refresh token
     *
     * @param request  from Servlet
     * @param response from Servlet
     * @return success or not. Return type is boolean.
     */
    @Deprecated
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
    {
        return subscribe(() -> ok(m_authenticationService.refreshToken(request, response)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Validate the token with given token.
     *
     * @param token represent the request token
     * @return success or not. Return type is boolean.
     */
    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestParam("token") String token)
    {
        return subscribe(() -> ok(m_authenticationService.validateToken(token)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }
}