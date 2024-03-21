package project.coffeeshop.menu;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.coffeeshop.authentication.User;

import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "volume", nullable = false)
    private int volume;

    @Column(name = "image", nullable = false)
    private String image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(price, menuItem.price) == 0 && volume == menuItem.volume && Objects.equals(id, menuItem.id) && Objects.equals(name, menuItem.name) && Objects.equals(image, menuItem.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, volume, image);
    }
}
