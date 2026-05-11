package spartaclub.cafeorderservice.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spartaclub.cafeorderservice.common.exception.CustomException;
import spartaclub.cafeorderservice.common.exception.ErrorCode;
import spartaclub.cafeorderservice.domain.user.dto.*;
import spartaclub.cafeorderservice.domain.user.entity.User;
import spartaclub.cafeorderservice.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // User 조회
    private User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }
        String encodedPin = passwordEncoder.encode(request.pin());
        User user = User.builder()
                .phone(request.phone())
                .pin(encodedPin)
                .build();
        return SignupResponse.from(userRepository.save(user));
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserPointResponse getMyInfo(String uuid) {
        User user = findUserByUuid(uuid);           // DB에서 최신값 조회
        return UserPointResponse.from(user);
    }

    // 전화번호 변경
    @Transactional
    public void updatePhone(String uuid, UpdatePhoneRequest request) {
        // DB에서 최신 User 조회 (pin 값도 정확하게 가져옴)
        User user = findUserByUuid(uuid);

        // 1. 현재 PIN 본인 확인
        if (!passwordEncoder.matches(request.currentPin(), user.getPin())) {
            throw new CustomException(ErrorCode.INVALID_PIN);  // PIN 틀리면 여기서 예외
        }

        // 2. 현재와 동일한 번호 체크
        if (user.getPhone().equals(request.newPhone())) {
            throw new CustomException(ErrorCode.SAME_PHONE);
        }

        // 3. 다른 사람이 이미 쓰는 번호인지 체크
        if (userRepository.existsByPhone(request.newPhone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        // 4. 변경 (더티 체킹으로 자동 UPDATE)
        user.updatePhone(request.newPhone());
    }

    // Pin 변경
    @Transactional
    public void updatePin(String uuid, UpdatePinRequest request) {
        // DB에서 최신 User 조회
        User user = findUserByUuid(uuid);

        // 1. 현재 PIN 검증
        if (!passwordEncoder.matches(request.currentPin(), user.getPin())) {
            throw new CustomException(ErrorCode.INVALID_PIN);
        }

        // 2. 새 PIN 암호화 후 저장
        user.updatePin(passwordEncoder.encode(request.newPin()));
    }
}
