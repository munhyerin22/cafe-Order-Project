package spartaclub.cafeorderservice.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spartaclub.cafeorderservice.domain.BaseEntity;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name ="users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private Long point = 0L;

    @Builder
    private User(String phone, String pin) {
        this.uuid = UUID.randomUUID().toString();
        this.phone = phone;
        this.pin = pin;
        this.point = 0L;
    }

    // 포인트 충전
    public void chargePoint(Long amount) {
        this.point += amount;
    }

    // 포인트 차감
    public void deductPoint(Long amount) {
        this.point -= amount;
    }

    // 전화번호 변경
    public void updatePhone(String newPhone) {
        this.phone = newPhone;
    }

    // PIN 변경: 이미 암호화된 새 PIN 값을 받아서 저장
    public void updatePin(String encodedNewPin) {
        this.pin = encodedNewPin;
    }
}
