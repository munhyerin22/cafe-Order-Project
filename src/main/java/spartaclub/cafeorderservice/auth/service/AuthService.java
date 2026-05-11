package spartaclub.cafeorderservice.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spartaclub.cafeorderservice.auth.dto.LoginRequest;
import spartaclub.cafeorderservice.auth.dto.LoginResponse;
import spartaclub.cafeorderservice.auth.security.JwtTokenProvider;
import spartaclub.cafeorderservice.common.exception.CustomException;
import spartaclub.cafeorderservice.common.exception.ErrorCode;
import spartaclub.cafeorderservice.domain.user.entity.User;
import spartaclub.cafeorderservice.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인 : 전화번호 + PIN 검증 -> JWT 발급
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 1. 전화번호로 사용자 조회(없으면 Auth_Failed)
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_FAILED));

        // 2. BCrypt.matches(입력값, DB에 저장된 해시값)
        if (!passwordEncoder.matches(request.pin(), user.getPin())) {
            throw new CustomException(ErrorCode.AUTH_FAILED);
        }

        // 3. 사용자의 uuid를 token에 담아서 반환
        String accessToken = jwtTokenProvider.generateToken(user.getUuid());

        return new LoginResponse(accessToken);

    }
}
