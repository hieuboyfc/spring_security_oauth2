package zimji.hieuboy.oauth2.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import zimji.hieuboy.oauth2.config.security.CurrentUser;
import zimji.hieuboy.oauth2.config.security.UserPrincipal;
import zimji.hieuboy.oauth2.entity.User;
import zimji.hieuboy.oauth2.exception.ResourceNotFoundException;
import zimji.hieuboy.oauth2.repository.IUserRepository;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 23:12
 */

@RestController
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userPrincipal.getUsername()));
    }

}
