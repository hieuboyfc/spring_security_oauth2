package zimji.hieuboy.oauth2.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:19
 */

@Accessors(fluent = true)
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();

    private final Account account = new Account();

    private final EmailConfig emailConfig = new EmailConfig();

    @Data
    public static class Auth {

        private String tokenSecret; // Ký hiệu token
        private long tokenExpirationMsec; // Thời gian token

    }

    @Data
    public static class Account {

        private String emailInvalidRegex; // Định dạng địa chỉ email chính xác
        private String usernameSymbolRegex; // Kiểm tra ký tự đặc biệt cho username
        private Integer passwordExpirationInDay; // Thời gian hết hạn mật khẩu
        private long resetTotpExpirationInMs; // Hết hạn mã TOTP 1 ngày
        private long activeTotpExpirationInMs; // Hết hạn mã TOTP 7 ngày
        private int codeDigits; // 8 Mã số

    }

    @Data
    public static class EmailConfig {

        private String folderEmailDefault; // Thưc mục mặc định của Email Template: '/email_templates/'
        private String fileEmailActiveAccount; // Tệp tin email thông báo kích hoạt
        private String fileEmailChangePassword; // Tệp tin email thay đổi mật khẩu
        private String fileEmailExpirationPassword; // Tệp tin email thông báo hết hạn mật khẩu
        private String fileEmailResetPassword; // Tệp tin email đặt lại mật khẩu
        private String fileEmailSignin; // Tệp tin email thông báo đăng nhập
        private String fileEmailSignupActiveAccount; // Tệp tin email kích hoạt tài khoản sau khi đăng ký tài khoản
        private String activeLinkInEmail; // Link active tài khoản qua địa chỉ email
        private String resetLinkInEmail; //  Link reset tài khoản qua địa chỉ email
        private boolean sendEmailNotifyWhenLogin; // Gửi email khi đăng nhập
        private boolean sendEmailNotifyWhenSignUp; // Gửi email khi đăng ký

    }

    public Auth getAuth() {
        return auth;
    }

    public Account getAccount() {
        return account;
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

}
