package spartaclub.cafeorderservice.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spartaclub.cafeorderservice.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
