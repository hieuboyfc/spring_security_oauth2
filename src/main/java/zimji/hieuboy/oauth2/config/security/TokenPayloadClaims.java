package zimji.hieuboy.oauth2.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import zimji.hieuboy.oauth2.entity.User;
import zimji.hieuboy.oauth2.exception.BadRequestException;
import zimji.hieuboy.oauth2.util.RequestClientInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 17/08/2020 - 12:42
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPayloadClaims implements Serializable {

    private static final long serialVersionUID = 1L;

    private String iss;// Issuer
    private Date exp;// Expiration Time
    private Date nbf;// Not Before
    private Date iat;// Issued At
    private String uid; // User ID
    private String ufn; // User FullName
    private String did; // Device ID
    private Long lcp; // Last Change Pass
    private Integer expirationDate; // Ngày hết hạn

    public TokenPayloadClaims(User user, Integer passwordExpInDay, HttpServletRequest request) {
        this.iss("JWTSuperSecretKey"); // Key
        this.exp(new Date(new Date().getTime() + 604800000)); // Hết hạn Token 7 Ngày
        this.nbf(new Date());
        this.iat(new Date());
        this.did(RequestClientInfo.getInstance().getIdentifyDevice(request));
        if (user == null) {
            throw new BadRequestException("Không tìm thấy thông tin tài khoản.");
        }
        this.uid(user.username());
        this.ufn(user.fullName());
        this.lcp(user.lastPasswordChange().getTime());
        this.expirationDate(passwordExpInDay);
    }

}
