package spartaclub.cafeorderservice.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spartaclub.cafeorderservice.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 전화번호 중복 여부 확인 (회원가입 시 사용)
    boolean existsByPhone(String phone);

    // 전화번호로 사용자 찾기 (로그인 중복 체크에 사용)
    Optional<User> findByPhone(String phone);
}
