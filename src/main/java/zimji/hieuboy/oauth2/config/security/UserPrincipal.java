package zimji.hieuboy.oauth2.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import zimji.hieuboy.oauth2.entity.User;
import zimji.hieuboy.oauth2.payload.SocialProvider;

import java.io.Serializable;
import java.util.*;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:23
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;
    private String username;
    private String email;
    private String password;
    private String passwordDecrypt;
    private Date lastPasswordChange;
    private boolean emailVerified;
    private String imageUrl;
    private SocialProvider provider;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.passwordDecrypt = user.getPasswordDecrypt();
        this.lastPasswordChange = user.getLastPasswordChange();
        this.emailVerified = user.getEmailVerified();
        this.imageUrl = user.getImageUrl();
        this.provider = user.getProvider();
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new UserPrincipal(user, authorities);
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getName() {
        return fullName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public void setProvider(SocialProvider provider) {
        this.provider = provider;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
