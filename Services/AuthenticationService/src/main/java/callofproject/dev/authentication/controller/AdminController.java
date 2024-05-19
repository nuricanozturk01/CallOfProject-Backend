package callofproject.dev.authentication.controller;

import callofproject.dev.authentication.dto.admin.UserUpdateDTOAdmin;
import callofproject.dev.authentication.dto.auth.AuthenticationRequest;
import callofproject.dev.authentication.service.admin.AdminService;
import callofproject.dev.data.common.clas.ErrorMessage;
import callofproject.dev.data.common.clas.MultipleResponseMessagePageable;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("api/auth/admin")
@SecurityRequirement(name = "Authorization")
public class AdminController
{
    private final AdminService m_adminService;


    /**
     * Constructor for the AdminController class.
     * It is used to inject dependencies into the controller.
     *
     * @param adminService The AdminService object to be injected.
     */
    public AdminController(AdminService adminService)
    {
        m_adminService = adminService;
    }

    /**
     * Login operation for admins.
     *
     * @param request represent the login information.
     * @return if success returns AuthenticationResponse that include token and status else return ErrorMessage.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticationRequest request)
    {
        return subscribe(() -> ok(m_adminService.authenticate(request)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 401)));
    }

    /**
     * Find all users page by page
     *
     * @param page is page
     * @return UserShowingAdminDTO
     */
    @GetMapping("find/all/page")
    public ResponseEntity<Object> findAllUserByPage(@RequestParam("p") int page)
    {
        return subscribe(() -> ok(m_adminService.findAllUsersPageable(page)), msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }


    /**
     * Find all users page by page
     *
     * @return UserShowingAdminDTO
     */
    @GetMapping("find/all")
    public ResponseEntity<Object> findAllUsers()
    {
        return subscribe(() -> ok(m_adminService.findAllUsers()), msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }


    /**
     * Find all users with given parameters are word and page
     *
     * @param page is page
     * @param word contains word
     * @return Users Showing Admin DTO
     */
    @GetMapping("find/all/contains/page")
    public ResponseEntity<Object> findUsersByUsernameContainsIgnoreCase(@RequestParam("p") int page, String word)
    {
        return subscribe(() -> ok(m_adminService.findUsersByUsernameContainsIgnoreCase(page, word)),
                msg -> internalServerError().body(new MultipleResponseMessagePageable<>(0, 0, 0, "", null)));
    }

    /**
     * Find all users with given parameters are word and page
     *
     * @param page is page
     * @param word not contains word
     * @return Users Showing Admin DTO
     */
    @GetMapping("find/all/ignore/page")
    public ResponseEntity<Object> findUsersByUsernameNotContainsIgnoreCase(@RequestParam("p") int page, String word)
    {
        return subscribe(() -> ok(m_adminService.findUsersByUsernameNotContainsIgnoreCase(page, word)),
                msg -> internalServerError().body(new ErrorMessage("Users Not Found!", false, 500)));
    }

    /**
     * remove user with given username parameter
     *
     * @param username represent the user
     * @return success or not
     */
    @DeleteMapping("remove/user")
    public ResponseEntity<Object> removeUserByUsername(@RequestParam("uname") String username)
    {
        return subscribe(() -> ok(m_adminService.removeUser(username)),
                msg -> internalServerError().body(new ErrorMessage("User not removed!", false, 500)));
    }

    /**
     * Update user with given information.
     *
     * @param userUpdateDTO represent the user information.
     * @return the UserShowingAdminDTO.
     */
    @PutMapping("update/user")
    public ResponseEntity<Object> updateUserByUsername(@RequestBody UserUpdateDTOAdmin userUpdateDTO)
    {
        return subscribe(() -> ok(m_adminService.updateUser(userUpdateDTO)),
                msg -> internalServerError().body(new ErrorMessage(msg.getMessage(), false, 500)));
    }

    /**
     * Find Total User Count.
     *
     * @return the total user count
     */
    @GetMapping("find/user/all/count")
    public ResponseEntity<Object> findAllUserCount()
    {
        return subscribe(() -> ok(m_adminService.findAllUserCount()),
                msg -> internalServerError().body(new ErrorMessage("Users Not Found!", false, 500)));
    }

    /**
     * Find new user last n day.
     *
     * @param day represent the day
     * @return the new user count
     */
    @GetMapping("find/user/all/new")
    public ResponseEntity<Object> findNewUserCount(@RequestParam("n") int day)
    {
        return subscribe(() -> ok(m_adminService.findNewUsersLastNday(day)),
                msg -> internalServerError().body(new ErrorMessage("Users Not Found!", false, 500)));
    }
}
