package zimji.hieuboy.oauth2.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 23:05
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(min = 6, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
