package MobilLax.Domain.User.Model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * ✅ 로그인 인증을 위한 UserDetails 구현체
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private final UserAccount user;

    public CustomUserDetails(UserAccount user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 또는 user.getId().toString()을 customerId로 사용해도 됨
    }

    @Override public String getPassword() { return user.getPassword(); }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // 권한 처리 필요 시 수정
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
