package zimji.hieuboy.oauth2.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 23:06
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

}
