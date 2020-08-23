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

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenExpirationMsec() {
            return tokenExpirationMsec;
        }

        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }
    }

    @Data
    public static class Account {

        private String emailInvalidRegex; // Định dạng địa chỉ email chính xác
        private String usernameSymbolRegex; // Kiểm tra ký tự đặc biệt cho username
        private Integer passwordExpirationInDay; // Thời gian hết hạn mật khẩu
        private long resetTotpExpirationInMs; // Hết hạn mã TOTP 1 ngày
        private long activeTotpExpirationInMs; // Hết hạn mã TOTP 7 ngày
        private int codeDigits; // 8 Mã số
        private Integer expirationInMs; // Hết hạn 600000ms = 10p

        public String getEmailInvalidRegex() {
            return emailInvalidRegex;
        }

        public void setEmailInvalidRegex(String emailInvalidRegex) {
            this.emailInvalidRegex = emailInvalidRegex;
        }

        public String getUsernameSymbolRegex() {
            return usernameSymbolRegex;
        }

        public void setUsernameSymbolRegex(String usernameSymbolRegex) {
            this.usernameSymbolRegex = usernameSymbolRegex;
        }

        public Integer getPasswordExpirationInDay() {
            return passwordExpirationInDay;
        }

        public void setPasswordExpirationInDay(Integer passwordExpirationInDay) {
            this.passwordExpirationInDay = passwordExpirationInDay;
        }

        public long getResetTotpExpirationInMs() {
            return resetTotpExpirationInMs;
        }

        public void setResetTotpExpirationInMs(long resetTotpExpirationInMs) {
            this.resetTotpExpirationInMs = resetTotpExpirationInMs;
        }

        public long getActiveTotpExpirationInMs() {
            return activeTotpExpirationInMs;
        }

        public void setActiveTotpExpirationInMs(long activeTotpExpirationInMs) {
            this.activeTotpExpirationInMs = activeTotpExpirationInMs;
        }

        public int getCodeDigits() {
            return codeDigits;
        }

        public void setCodeDigits(int codeDigits) {
            this.codeDigits = codeDigits;
        }

        public Integer getExpirationInMs() {
            return expirationInMs;
        }

        public void setExpirationInMs(Integer expirationInMs) {
            this.expirationInMs = expirationInMs;
        }
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

        public String getFolderEmailDefault() {
            return folderEmailDefault;
        }

        public void setFolderEmailDefault(String folderEmailDefault) {
            this.folderEmailDefault = folderEmailDefault;
        }

        public String getFileEmailActiveAccount() {
            return fileEmailActiveAccount;
        }

        public void setFileEmailActiveAccount(String fileEmailActiveAccount) {
            this.fileEmailActiveAccount = fileEmailActiveAccount;
        }

        public String getFileEmailChangePassword() {
            return fileEmailChangePassword;
        }

        public void setFileEmailChangePassword(String fileEmailChangePassword) {
            this.fileEmailChangePassword = fileEmailChangePassword;
        }

        public String getFileEmailExpirationPassword() {
            return fileEmailExpirationPassword;
        }

        public void setFileEmailExpirationPassword(String fileEmailExpirationPassword) {
            this.fileEmailExpirationPassword = fileEmailExpirationPassword;
        }

        public String getFileEmailResetPassword() {
            return fileEmailResetPassword;
        }

        public void setFileEmailResetPassword(String fileEmailResetPassword) {
            this.fileEmailResetPassword = fileEmailResetPassword;
        }

        public String getFileEmailSignin() {
            return fileEmailSignin;
        }

        public void setFileEmailSignin(String fileEmailSignin) {
            this.fileEmailSignin = fileEmailSignin;
        }

        public String getFileEmailSignupActiveAccount() {
            return fileEmailSignupActiveAccount;
        }

        public void setFileEmailSignupActiveAccount(String fileEmailSignupActiveAccount) {
            this.fileEmailSignupActiveAccount = fileEmailSignupActiveAccount;
        }

        public String getActiveLinkInEmail() {
            return activeLinkInEmail;
        }

        public void setActiveLinkInEmail(String activeLinkInEmail) {
            this.activeLinkInEmail = activeLinkInEmail;
        }

        public String getResetLinkInEmail() {
            return resetLinkInEmail;
        }

        public void setResetLinkInEmail(String resetLinkInEmail) {
            this.resetLinkInEmail = resetLinkInEmail;
        }

        public boolean isSendEmailNotifyWhenLogin() {
            return sendEmailNotifyWhenLogin;
        }

        public void setSendEmailNotifyWhenLogin(boolean sendEmailNotifyWhenLogin) {
            this.sendEmailNotifyWhenLogin = sendEmailNotifyWhenLogin;
        }

        public boolean isSendEmailNotifyWhenSignUp() {
            return sendEmailNotifyWhenSignUp;
        }

        public void setSendEmailNotifyWhenSignUp(boolean sendEmailNotifyWhenSignUp) {
            this.sendEmailNotifyWhenSignUp = sendEmailNotifyWhenSignUp;
        }
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
