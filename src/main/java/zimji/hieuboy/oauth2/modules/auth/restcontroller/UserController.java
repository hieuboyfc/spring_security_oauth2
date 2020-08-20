package zimji.hieuboy.oauth2.modules.auth.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import zimji.hieuboy.oauth2.configs.security.CurrentUser;
import zimji.hieuboy.oauth2.configs.security.UserPrincipal;
import zimji.hieuboy.oauth2.modules.auth.entity.UserEntity;
import zimji.hieuboy.oauth2.exceptions.ResourceNotFoundException;
import zimji.hieuboy.oauth2.modules.auth.repository.IUserRepository;

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
    public UserEntity getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userPrincipal.getUsername()));
    }

}
