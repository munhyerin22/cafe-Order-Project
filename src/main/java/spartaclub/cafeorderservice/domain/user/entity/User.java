package spartaclub.cafeorderservice.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Getter
@NoArgsConstructor
@Entity
@Table(name ="users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private Long point = 0L;

    @Builder
    private User(String phone, String pin) {
        this.phone = phone;
        this.pin = pin;
        this.point = 0L;
    }

    public void chargePoint(Long amount) {
        this.point += amount;
    }

    public void deductPoint(Long amount) {
        this.point -= amount;
    }

    public void updatePhone(String newPhone) {
        this.phone = newPhone;
    }
}
