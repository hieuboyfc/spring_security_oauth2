package zimji.hieuboy.oauth2.modules.auth.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 20/08/2020 - 17:20
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TOTPToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String totp;
    private long exp;

}
