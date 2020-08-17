package zimji.hieuboy.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:19
 */

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();

    private final Account account = new Account();

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

    public static class Account {

        private String passwordPolicyRegex; // Định dạng mật khẩu
        private String emailInvalidRegex; // Định dạng địa chỉ email chính xác
        private String usernameSymbolRegex; // Kiểm tra ký tự đặc biệt cho username
        private Integer passwordExpirationInDay; // Thời gian hết hạn mật khẩu

        public String getPasswordPolicyRegex() {
            return passwordPolicyRegex;
        }

        public void setPasswordPolicyRegex(String passwordPolicyRegex) {
            this.passwordPolicyRegex = passwordPolicyRegex;
        }

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
    }

    public Auth getAuth() {
        return auth;
    }

    public Account getAccount() {
        return account;
    }

}
