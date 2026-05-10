package spartaclub.cafeorderservice.domain.order.entity;

import jakarta.persistence.*;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Entity
@Table(name ="orderItems")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}