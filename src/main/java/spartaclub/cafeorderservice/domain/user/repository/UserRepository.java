package spartaclub.cafeorderservice.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spartaclub.cafeorderservice.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
