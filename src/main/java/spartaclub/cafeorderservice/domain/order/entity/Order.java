package spartaclub.cafeorderservice.domain.order.entity;

import jakarta.persistence.*;
import org.hibernate.metamodel.model.domain.IdentifiableDomainType;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Entity
@Table(name ="orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
