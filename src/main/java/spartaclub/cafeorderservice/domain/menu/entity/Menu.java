package spartaclub.cafeorderservice.domain.menu.entity;

import jakarta.persistence.*;
import spartaclub.cafeorderservice.domain.BaseEntity;

@Entity
@Table(name ="menus")
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
