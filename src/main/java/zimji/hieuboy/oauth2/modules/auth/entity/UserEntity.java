package zimji.hieuboy.oauth2.modules.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import zimji.hieuboy.oauth2.auditing.Auditing;
import zimji.hieuboy.oauth2.modules.auth.type.SocialProvider;
import zimji.hieuboy.oauth2.utils.SecurityUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:06
 */

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends Auditing implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tên tài khoản
    @Basic
    @Id
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    // Họ và tên
    @Basic
    @Column(name = "full_name", length = 50, nullable = false)
    private String fullName;

    // Địa chỉ email
    @Basic
    @Email
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    // Mật khẩu
    @Basic
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    // Mật khẩu mã hóa, có thể giải mã
    @Basic
    @Column(name = "password_decrypt", length = 100, nullable = false)
    private String passwordDecrypt;

    // Lần thay đổi mật khẩu cuối cùng
    @Basic
    @Column(name = "last_password_change", nullable = false)
    private Date lastPasswordChange;

    // Hình ảnh cá nhân
    @Basic
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // Xác nhận email, kích hoạt tải khoản
    @Basic
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    // Loại tài khoản
    @Basic
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private SocialProvider provider;

    // Mã khóa mật khẩu
    public void updatePassword(String password) {
        this.password = SecurityUtils.getEncryptPassword(password);
        this.passwordDecrypt = SecurityUtils.getInstance().encrypt(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordDecrypt() {
        return passwordDecrypt;
    }

    public void setPasswordDecrypt(String passwordDecrypt) {
        this.passwordDecrypt = passwordDecrypt;
    }

    public Date getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(Date lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public void setProvider(SocialProvider provider) {
        this.provider = provider;
    }
}
