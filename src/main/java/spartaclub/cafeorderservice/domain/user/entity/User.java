package spartaclub.cafeorderservice.domain.user.entity;

import jakarta.persistence.*;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Entity
@Table(name ="users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
