package spartaclub.cafeorderservice.auth.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spartaclub.cafeorderservice.domain.user.entity.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    // 실제 User 엔티티를 안에 보관
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    //이 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 권한 없음
    }


    // Spring Security가 사용하는 비밀번호 (PIN 해시값)
    @Override
    public String getPassword() {
        return user.getPin();
    }

    // Spring Security가 사용하는 사용자 식별값
    @Override
    public String getUsername() {
        return user.getUuid();
    }

    // 계정 상태 관련 메서드
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
