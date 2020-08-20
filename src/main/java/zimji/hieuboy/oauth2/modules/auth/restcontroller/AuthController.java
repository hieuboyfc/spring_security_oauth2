package zimji.hieuboy.oauth2.modules.auth.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import zimji.hieuboy.oauth2.modules.auth.entity.UserEntity;
import zimji.hieuboy.oauth2.modules.auth.payload.*;
import zimji.hieuboy.oauth2.modules.auth.service.AuthService;

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
    public ResponseEntity<?> signupUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserEntity userEntity = authService.signup(signUpRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(userEntity.getUsername()).toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Đăng ký tài khoản thành công.", userEntity));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Đổi mật khẩu cho người dùng thành công.", userEntity));
    }

    @PostMapping("/active-account")
    public ResponseEntity<?> activeAccount(@Valid @RequestBody String username,
                                           @Valid @RequestBody String totp) {
        UserEntity userEntity = authService.activeAccount(username, totp);
        return ResponseEntity.ok(new ApiResponse(true, "Kích hoạt tài khoản cho người dùng thành công.", userEntity));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                          HttpServletRequest request) {
        return ResponseEntity.ok(new AuthResponse(authService.refreshToken(token, request)));
    }

}
