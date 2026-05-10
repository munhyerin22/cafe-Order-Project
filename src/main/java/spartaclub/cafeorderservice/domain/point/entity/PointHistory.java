package spartaclub.cafeorderservice.domain.point.entity;

import jakarta.persistence.*;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Entity
@Table(name ="points")
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
