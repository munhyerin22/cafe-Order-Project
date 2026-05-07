package spartaclub.cafeorderservice.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spartaclub.cafeorderservice.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
