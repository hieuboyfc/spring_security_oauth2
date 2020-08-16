package zimji.hieuboy.oauth2.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zimji.hieuboy.oauth2.config.AppProperties;
import zimji.hieuboy.oauth2.config.security.TokenProvider;
import zimji.hieuboy.oauth2.config.security.UserPrincipal;
import zimji.hieuboy.oauth2.entity.User;
import zimji.hieuboy.oauth2.exception.BadRequestException;
import zimji.hieuboy.oauth2.exception.ResourceNotFoundException;
import zimji.hieuboy.oauth2.payload.*;
import zimji.hieuboy.oauth2.repository.IUserRepository;
import zimji.hieuboy.oauth2.util.Common;
import zimji.hieuboy.oauth2.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 16/08/2020 - 14:22
 */

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AppProperties appProperties;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // Cho phép mọi người đăng nhập bằng email hoặc username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Không tìm thấy tài khoản [%s]", username))
                );
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        return UserPrincipal.create(user);
    }

    /**
     * Đăng nhập tài khoản
     */
    public AuthResponse signin(LoginRequest loginRequest, HttpServletRequest request) {

        User user = validateInput(loginRequest.getUsername(), null, loginRequest.getPassword(), null, null, 2);

        if (user.lastPasswordChange() == null) {
            throw new BadRequestException("Không tìm thấy lần thay đổi mật khẩu lần cuối.");
        }
        if (Common.getDateDiff(user.lastPasswordChange(), new Date(), TimeUnit.DAYS)
                > Long.parseLong(appProperties.getAccount().getPasswordExpirationInDay())) {
            throw new BadRequestException(String.format(
                    "Mật khẩu đã quá hạn sử dụng (được sử dụng từ ngày [%s]) yêu cầu phải thay đổi mật khẩu để tiếp tục truy cập vào hệ thống.",
                    Common.convertDate2String(user.lastPasswordChange(), "DD/MM/YYYY")));

        }
        if (!user.getEmailVerified()) {
            throw new BadRequestException(String.format("Tài khoản [%s] chưa được kích hoạt.", user.getUsername()));
        }
        if (!SecurityUtils.checkEncryptPassword(loginRequest.password(), user.password())) {
            throw new BadRequestException("Sai thông tin mật khẩu.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new AuthResponse(tokenProvider.createToken(authentication));
    }

    /**
     * Đăng ký tài khoản
     */
    public User signup(SignUpRequest signUpRequest) {
        if (validateInput(signUpRequest.getUsername(), signUpRequest.email(), null,
                signUpRequest.password(), signUpRequest.confirmPassword(), 1) != null) {
            throw new BadRequestException("Đăng ký tài khoản thất bại.");
        }
        // Gán các giá trị cho tài khoản
        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setLastPasswordChange(new Date());
        user.updatePassword(signUpRequest.password());
        user.setProvider(SocialProvider.local);
        user = userRepository.save(user);
        return user;
    }

    /**
     * Đổi mật khẩu
     */
    public User changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = validateInput(changePasswordRequest.getUsername(), null,
                changePasswordRequest.oldPassword(), changePasswordRequest.newPassword(),
                changePasswordRequest.confirmPassword(), 3);
        if (StringUtils.isEmpty(user.password())) {
            throw new BadRequestException(String.format("Mật khẩu của người dùng [%s] không được tìm thấy.",
                    changePasswordRequest.getUsername()));
        }
        if (!StringUtils.isEmpty(user.password())
                && !SecurityUtils.checkEncryptPassword(changePasswordRequest.oldPassword(), user.password())) {
            throw new BadRequestException(String.format("Mật khẩu cũ của người dùng [%s] không đúng.",
                    changePasswordRequest.getUsername()));
        }
        if (!StringUtils.isEmpty(user.password())
                && SecurityUtils.checkEncryptPassword(changePasswordRequest.newPassword(), user.password())) {
            throw new BadRequestException(String.format("Mật khẩu mới của người dùng [%s] không được trùng với mật khẩu cũ.",
                    changePasswordRequest.getUsername()));
        }
        user.setLastPasswordChange(new Date());
        user.updatePassword(changePasswordRequest.getNewPassword());
        user = userRepository.save(user);
        return user;
    }

    private User validateInput(String username,
                               String email,
                               String oldPassword,
                               String newPassword,
                               String confirmPassword,
                               Integer type) {
        // Kiểm tra khi đăng ký tài khoản
        if (type == 1) {
            if (StringUtils.isEmpty(username)) {
                throw new BadRequestException("Tên đăng nhập không được để trống.");
            }
            if (Pattern.matches(appProperties.getAccount().getUsernameSymbolRegex(), username)) {
                throw new BadRequestException(String.format("Tên đăng nhập [%s] không được phép có ký tự đặc biệt.", username));
            }
            if (StringUtils.isEmpty(email)) {
                throw new BadRequestException("Địa chỉ email không được để trống.");
            }
            if (!Pattern.matches(appProperties.getAccount().getEmailInvalidRegex(), email)) {
                throw new BadRequestException(String.format("Địa chỉ email [%s] không đúng định dạng. [VD: hieuboyfc@gmail.com]", email));
            }
            if (StringUtils.isEmpty(newPassword)) {
                throw new BadRequestException("Mật khẩu không được để trống.");
            }
            if (StringUtils.isEmpty(confirmPassword)) {
                throw new BadRequestException("Mật khẩu xác nhận không được để trống.");
            }
            if (!StringUtils.isEmpty(confirmPassword) && !newPassword.equals(confirmPassword)) {
                throw new BadRequestException("Mật khẩu và mật khẩu xác nhận không trùng khớp.");
            }
            if (StringUtils.trim(newPassword).toLowerCase().contains(StringUtils.trim(username).toLowerCase())) {
                throw new BadRequestException(
                        String.format("Mật khẩu không được có phần trùng với tên tài khoản [%s].", username));
            }
        }

        // Kiểm tra khi đăng nhập tài khoản
        if (type == 2) {
            if (StringUtils.isEmpty(username)) {
                throw new BadRequestException("Tên đăng nhập hoặc Địa chỉ email không được để trống.");
            }
            if (StringUtils.isEmpty(oldPassword)) {
                throw new BadRequestException("Mật khẩu không được để trống.");
            }
        }

        // Kiểm tra khi đổi mật khẩu
        if (type == 3) {
            if (StringUtils.isEmpty(username)) {
                throw new BadRequestException("Tên đăng nhập không được để trống.");
            }
            if (Pattern.matches(appProperties.getAccount().getUsernameSymbolRegex(), username)) {
                throw new BadRequestException(String.format("Tên đăng nhập [%s] không được phép có ký tự đặc biệt.", username));
            }
            if (StringUtils.isEmpty(oldPassword)) {
                throw new BadRequestException("Mật khẩu cũ không được để trống.");
            }
            if (StringUtils.isEmpty(newPassword)) {
                throw new BadRequestException("Mật khẩu mới không được để trống.");
            }
            if (StringUtils.isEmpty(confirmPassword)) {
                throw new BadRequestException("Mật khẩu xác nhận không được để trống.");
            }
            if (!StringUtils.isEmpty(confirmPassword) && !newPassword.equals(confirmPassword)) {
                throw new BadRequestException("Mật khẩu mới và mật khẩu xác nhận không trùng khớp.");
            }
            if (StringUtils.trim(newPassword).toLowerCase().contains(StringUtils.trim(username).toLowerCase())) {
                throw new BadRequestException(
                        String.format("Mật khẩu mới không được có phần trùng với tên tài khoản [%s].", username));
            }
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if ((type == 2 || type == 3) && user == null) {
            throw new BadRequestException(String.format("Tên tài khoản [%s] không tồn tại trong hệ thống", username));
        }
        if (type == 1 && user != null) {
            throw new BadRequestException(String.format("Tên tài khoản [%s] đã tồn tại trong hệ thống", username));
        }
        return user;
    }
}
