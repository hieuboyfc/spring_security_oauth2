package zimji.hieuboy.oauth2.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import zimji.hieuboy.oauth2.modules.auth.payload.TOTPToken;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 19/08/2020 - 17:22
 */

public class TOTP {

    public static final GoogleAuthenticator googleAuth = new GoogleAuthenticator();
    private static TOTP instance = new TOTP();

    public static TOTP getInstance() {
        return instance;
    }

    private TOTP() {

    }

    public String getUrlWithTOTPByUsername(String url, String username, long timeStepSizeInMillis, int codeDigits) {
        TOTPToken totpToken = new TOTPToken().username(username)
                .totp(TOTP.getInstance().getTOTPByUsername(username, timeStepSizeInMillis, codeDigits))
                .exp(TOTP.getInstance().getTOTPExp(timeStepSizeInMillis));
        return String.format("%s?token=%s", url, Base64.encodeBase64String(
                JacksonUtils.getInstance().object2String(totpToken).getBytes(StandardCharsets.UTF_8)));
    }

    public String getTOTPByUsername(String username, long timeStepSizeInMillis, int codeDigits) {
        return getTOTPBySecret(getSecretByUsername(username), timeStepSizeInMillis, codeDigits);
    }

    public String getTOTPBySecret(String secret, long timeStepSizeInMillis, int codeDigits) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(timeStepSizeInMillis).setCodeDigits(codeDigits).build());
        return StringUtils.leftPad(Integer.toString(googleAuthenticator.getTotpPassword(secret)), codeDigits, "0");
    }

    public long getTOTPExp(long timeStepSizeInMillis) {
        return new Date().getTime() + timeStepSizeInMillis;
    }


    public Boolean checkTOTP(String username, String totp, long timeStepSizeInMillis, int codeDigits) {
        boolean result = false;
        totp = StringUtils.trim(totp);
        if (timeStepSizeInMillis <= 0 && totp.length() < 6 || totp.length() > codeDigits) {
            return result;
        }
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(timeStepSizeInMillis).setCodeDigits(codeDigits).build());
        String secret = getSecretByUsername(username);
        result = googleAuthenticator.authorize(secret, Integer.parseInt(totp));
        if (!result) {
            result = googleAuth.authorize(secret, Integer.parseInt(totp.substring(0, 6)));
        }
        return result;
    }

    public String getSecretByUsername(String username) {
        return DigestUtils.md5Hex(username);
    }

}
