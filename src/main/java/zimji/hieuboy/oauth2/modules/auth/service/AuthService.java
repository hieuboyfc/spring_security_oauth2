package zimji.hieuboy.oauth2.modules.auth.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zimji.hieuboy.oauth2.configs.AppProperties;
import zimji.hieuboy.oauth2.configs.security.TokenPayloadClaims;
import zimji.hieuboy.oauth2.configs.security.TokenProvider;
import zimji.hieuboy.oauth2.configs.security.UserPrincipal;
import zimji.hieuboy.oauth2.modules.auth.entity.UserEntity;
import zimji.hieuboy.oauth2.exceptions.BadRequestException;
import zimji.hieuboy.oauth2.exceptions.ResourceNotFoundException;
import zimji.hieuboy.oauth2.modules.auth.payload.AuthResponse;
import zimji.hieuboy.oauth2.modules.auth.payload.ChangePasswordRequest;
import zimji.hieuboy.oauth2.modules.auth.payload.LoginRequest;
import zimji.hieuboy.oauth2.modules.auth.payload.SignUpRequest;
import zimji.hieuboy.oauth2.modules.auth.repository.IUserRepository;
import zimji.hieuboy.oauth2.modules.auth.type.SocialProvider;
import zimji.hieuboy.oauth2.modules.email.EmailOutboxService;
import zimji.hieuboy.oauth2.utils.Common;
import zimji.hieuboy.oauth2.utils.RequestClientInfo;
import zimji.hieuboy.oauth2.utils.SecurityUtils;
import zimji.hieuboy.oauth2.utils.TOTP;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 16/08/2020 - 14:22
 */

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private IUserRepository userRepository;

    private TokenProvider tokenProvider;

    private AppProperties appProperties;

    private EmailOutboxService emailOutboxService;

    @Autowired
    public AuthService(IUserRepository userRepository,
                       TokenProvider tokenProvider,
                       AppProperties appProperties,
                       EmailOutboxService emailOutboxService) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.emailOutboxService = emailOutboxService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // Cho phép mọi người đăng nhập bằng email hoặc username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Không tìm thấy tài khoản [%s]", username))
                );
        return UserPrincipal.create(userEntity);
    }

    @Transactional
    public UserDetails loadUserById(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        return UserPrincipal.create(userEntity);
    }

    /**
     * Đăng nhập tài khoản
     */
    public AuthResponse signin(LoginRequest loginRequest, HttpServletRequest request) {

        UserEntity userEntity = validateInput(loginRequest.getUsername(), null, loginRequest.getPassword(), null, null, 2);

        if (userEntity.lastPasswordChange() == null) {
            throw new BadRequestException("Không tìm thấy lần thay đổi mật khẩu lần cuối.");
        }
        Integer passwordExpirationInDay = appProperties.getAccount().passwordExpirationInDay();
        if (Common.getDateDiff(userEntity.lastPasswordChange(), new Date(), TimeUnit.DAYS) > passwordExpirationInDay) {
            throw new BadRequestException(String.format(
                    "Mật khẩu đã quá hạn sử dụng (được sử dụng từ ngày [%s]) yêu cầu phải thay đổi mật khẩu để tiếp tục truy cập vào hệ thống.",
                    Common.convertDate2String(userEntity.lastPasswordChange(), "DD/MM/YYYY")));

        }
        if (!userEntity.getEmailVerified()) {
            throw new BadRequestException(String.format("Tài khoản [%s] chưa được kích hoạt.", userEntity.getUsername()));
        }
        if (!SecurityUtils.checkEncryptPassword(loginRequest.password(), userEntity.password())) {
            throw new BadRequestException("Sai thông tin mật khẩu.");
        }
        if (appProperties.getEmailConfig().sendEmailNotifyWhenLogin()) {
            try {
                Map<String, String> mapEmailTemplate = new HashMap<>();
                mapEmailTemplate.put("APP_CODE", "ZimJi");
                mapEmailTemplate.put("USERNAME", userEntity.username());
                emailOutboxService.insertItem(userEntity.email(),
                        String.format("[%s] Thông báo đăng nhập tài khoản vào hệ thống", "ZimJi"),
                        appProperties.getEmailConfig().fileEmailSignin(), mapEmailTemplate);
            } catch (Exception e) {
                logger.error("[ERROR] Không thể lưu thông tin dữ liệu Email khi đăng nhập {}", e.getMessage());
            }
        }
        /*Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);*/
        return new AuthResponse(tokenProvider.createToken(new TokenPayloadClaims(userEntity, passwordExpirationInDay, request)));
    }

    /**
     * Đăng ký tài khoản
     */
    public UserEntity signup(SignUpRequest signUpRequest) {
        if (validateInput(signUpRequest.getUsername(), signUpRequest.email(), null,
                signUpRequest.password(), signUpRequest.confirmPassword(), 1) != null) {
            throw new BadRequestException("Đăng ký tài khoản thất bại.");
        }
        // Gán các giá trị cho tài khoản
        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(signUpRequest.getFullName());
        userEntity.setUsername(signUpRequest.getUsername());
        userEntity.setEmail(signUpRequest.getEmail());
        userEntity.setLastPasswordChange(new Date());
        userEntity.updatePassword(signUpRequest.password());
        userEntity.setProvider(SocialProvider.local);
        userEntity = userRepository.save(userEntity);
        if (!userEntity.emailVerified()) {
            try {
                Map<String, String> mapEmailTemplate = new HashMap<>();
                mapEmailTemplate.put("APP_CODE", "ZimJi");
                mapEmailTemplate.put("USERNAME", userEntity.username());
                mapEmailTemplate.put("ACCOUNT_ACTIVE_LINK",
                        TOTP.getInstance().getUrlWithTOTPByUsername(
                                appProperties.getEmailConfig().activeLinkInEmail(), userEntity.username(),
                                appProperties.getAccount().activeTotpExpirationInMs(),
                                appProperties.getAccount().codeDigits()));
                emailOutboxService.insertItem(userEntity.email(),
                        String.format("[%s] Thông báo đăng ký tài khoản trên hệ thống", "ZimJi"),
                        appProperties.getEmailConfig().fileEmailSignupActiveAccount(), mapEmailTemplate);
            } catch (Exception e) {
                logger.error("[ERROR] Không thể lưu thông tin dữ liệu Email khi đăng ký tài khoản {}", e.getMessage());
            }
        }
        return userEntity;
    }

    /**
     * Đổi mật khẩu
     */
    public UserEntity changePassword(ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = validateInput(changePasswordRequest.getUsername(), null,
                changePasswordRequest.oldPassword(), changePasswordRequest.newPassword(),
                changePasswordRequest.confirmPassword(), 3);
        if (StringUtils.isEmpty(userEntity.password())) {
            throw new BadRequestException(String.format("Mật khẩu của người dùng [%s] không được tìm thấy.",
                    changePasswordRequest.getUsername()));
        }
        if (!StringUtils.isEmpty(userEntity.password())
                && !SecurityUtils.checkEncryptPassword(changePasswordRequest.oldPassword(), userEntity.password())) {
            throw new BadRequestException(String.format("Mật khẩu cũ của người dùng [%s] không đúng.",
                    changePasswordRequest.getUsername()));
        }
        if (!StringUtils.isEmpty(userEntity.password())
                && SecurityUtils.checkEncryptPassword(changePasswordRequest.newPassword(), userEntity.password())) {
            throw new BadRequestException(String.format("Mật khẩu mới của người dùng [%s] không được trùng với mật khẩu cũ.",
                    changePasswordRequest.getUsername()));
        }
        userEntity.setLastPasswordChange(new Date());
        userEntity.updatePassword(changePasswordRequest.getNewPassword());
        userEntity = userRepository.save(userEntity);
        try {
            Map<String, String> mapEmailTemplate = new HashMap<>();
            mapEmailTemplate.put("APP_CODE", "ZimJi");
            mapEmailTemplate.put("USERNAME", userEntity.username());
            emailOutboxService.insertItem(userEntity.email(),
                    String.format("[%s] Thông báo thay đổi mật khẩu cho tài khoản trên hệ thống", "ZimJi"),
                    appProperties.getEmailConfig().fileEmailChangePassword(), mapEmailTemplate);
        } catch (Exception e) {
            logger.error("[ERROR] Không thể lưu thông tin dữ liệu Email khi thay đổi mật khẩu cho tài khoản {}", e.getMessage());
        }
        return userEntity;
    }

    /**
     * Kích hoạt tài khoản
     */
    public UserEntity activeAccount(String username, String totp) {
        if (!TOTP.getInstance().checkTOTP(username, totp,
                appProperties.getAccount().activeTotpExpirationInMs(),
                appProperties.getAccount().codeDigits())) {
            throw new BadRequestException("Mã TOTP không hợp lệ.");
        }
        UserEntity userEntity = validateInput(username, null, null, null, null, 4);
        if (userEntity.emailVerified()) {
            throw new BadRequestException(
                    String.format("Tên tài khoản [%s] đã được kích hoạt trên hệ thống từ trước.", username));
        }
        userEntity.emailVerified(true);
        if (userEntity.emailVerified()) {
            try {
                Map<String, String> mapEmailTemplate = new HashMap<>();
                mapEmailTemplate.put("APP_CODE", "ZimJi");
                mapEmailTemplate.put("USERNAME", userEntity.username());
                emailOutboxService.insertItem(userEntity.email(),
                        String.format("[%s] Thông báo kích hoạt tài khoản trên hệ thống", "ZimJi"),
                        appProperties.getEmailConfig().fileEmailActiveAccount(), mapEmailTemplate);
            } catch (Exception e) {
                logger.error("[ERROR] Không thể lưu thông tin dữ liệu Email khi kích hoạt tài khoản {}", e.getMessage());
            }
        }
        return userRepository.save(userEntity);
    }

    /**
     * Làm mới lại Token
     */
    public String refreshToken(String token, HttpServletRequest request) {
        try {
            TokenPayloadClaims tokenPayloadClaims = tokenProvider.verifyTokenGetInfo(token);
            if (tokenPayloadClaims == null) {
                throw new BadCredentialsException("Token payload claims không được trống.");
            }
            if (!RequestClientInfo.getInstance().getIdentifyDevice(request).equals(tokenPayloadClaims.did())) {
                throw new BadCredentialsException("Thiết bị không hợp lệ.");
            }
            UserPrincipal userPrincipal = (UserPrincipal) loadUserById(tokenPayloadClaims.uid());
            if (userPrincipal.getLastPasswordChange() == null || tokenPayloadClaims.lcp() == null
                    || userPrincipal.getLastPasswordChange().getTime() != tokenPayloadClaims.lcp()) {
                throw new CredentialsExpiredException("Thông tin đã thay đổi.");
            }
            return tokenProvider.refreshToken(token);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private UserEntity validateInput(String username,
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
            if (Pattern.matches(appProperties.getAccount().usernameSymbolRegex(), username)) {
                throw new BadRequestException(String.format("Tên đăng nhập [%s] không được phép có ký tự đặc biệt.", username));
            }
            if (StringUtils.isEmpty(email)) {
                throw new BadRequestException("Địa chỉ email không được để trống.");
            }
            if (!Pattern.matches(appProperties.getAccount().emailInvalidRegex(), email)) {
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
            if (Pattern.matches(appProperties.getAccount().usernameSymbolRegex(), username)) {
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

        // Kiểm tra khi kích hoạt tài khoản
        if (type == 4) {
            if (StringUtils.isEmpty(username)) {
                throw new BadRequestException("Tên đăng nhập không được để trống.");
            }
            if (Pattern.matches(appProperties.getAccount().usernameSymbolRegex(), username)) {
                throw new BadRequestException(String.format("Tên đăng nhập [%s] không được phép có ký tự đặc biệt.", username));
            }
        }

        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if ((type == 2 || type == 3 || type == 4) && userEntity == null) {
            throw new BadRequestException(String.format("Tên tài khoản [%s] không tồn tại trong hệ thống", username));
        }
        if (type == 1 && userEntity != null) {
            throw new BadRequestException(String.format("Tên tài khoản [%s] đã tồn tại trong hệ thống", username));
        }
        return userEntity;
    }
}
