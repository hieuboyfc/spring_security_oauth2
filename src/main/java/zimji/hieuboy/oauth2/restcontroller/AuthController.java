package zimji.hieuboy.oauth2.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import zimji.hieuboy.oauth2.entity.User;
import zimji.hieuboy.oauth2.payload.ApiResponse;
import zimji.hieuboy.oauth2.payload.ChangePasswordRequest;
import zimji.hieuboy.oauth2.payload.LoginRequest;
import zimji.hieuboy.oauth2.payload.SignUpRequest;
import zimji.hieuboy.oauth2.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 23:03
 */

@RestController
@RequestMapping("/api/auth")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request) {
        return ResponseEntity.ok(authService.signin(loginRequest, request));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@Valid @RequestBody SignUpRequest signUpRequest) throws Exception {
        User user = authService.signup(signUpRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(user.getUsername()).toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Dăng ký tài khoản thành công.", user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
        User user = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Đổi mật khẩu cho người dùng thành công.", user));
    }

}
