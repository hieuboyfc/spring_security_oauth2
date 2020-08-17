package zimji.hieuboy.oauth2.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 17/08/2020 - 11:23
 */

@Component
@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.DEFAULT)
public class ScopeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String iss; // Issuer
    private Date exp; // Thời gian hết hạn token
    private Date nbf; // Not Before
    private Date iat; // Issued At
    private String uid; // Tên đăng nhập
    private String ufn; // Tên đầy đủ
    private String did; // Tên đầy đủ
    private String menuCode;
    private String menuAction;
    private String ipAddress; // Địa chỉ IP
    private String userAgent;
    private String urlRequest;
    private String urlReferer;

}
