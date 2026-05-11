package spartaclub.cafeorderservice.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spartaclub.cafeorderservice.common.exception.CustomException;
import spartaclub.cafeorderservice.common.exception.ErrorCode;
import spartaclub.cafeorderservice.domain.user.entity.User;
import spartaclub.cafeorderservice.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        // uuid로 사용자 조회, 없으면 예외 발생
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티를 Spring Security가 이해하는 UserDetails 형태로 감싸서 반환
        return new CustomUserDetails(user);
    }
}
