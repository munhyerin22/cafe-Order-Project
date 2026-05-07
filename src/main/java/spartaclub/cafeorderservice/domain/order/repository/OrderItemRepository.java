package spartaclub.cafeorderservice.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spartaclub.cafeorderservice.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
