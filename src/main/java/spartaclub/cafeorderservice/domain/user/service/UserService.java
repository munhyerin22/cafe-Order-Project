package spartaclub.cafeorderservice.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spartaclub.cafeorderservice.common.exception.CustomException;
import spartaclub.cafeorderservice.common.exception.ErrorCode;
import spartaclub.cafeorderservice.domain.user.dto.SignupRequest;
import spartaclub.cafeorderservice.domain.user.dto.SignupResponse;
import spartaclub.cafeorderservice.domain.user.entity.User;
import spartaclub.cafeorderservice.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. PIN 암호화
        String encodedPin = passwordEncoder.encode(request.pin());

        // 2. 전화번호 중복
        if(userRepository.existsByPhone(request.phone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        User user = User.builder()
                .phone(request.phone())
                .pin(encodedPin)
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }
}
