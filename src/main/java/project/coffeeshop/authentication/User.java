package project.coffeeshop.authentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.coffeeshop.menu.MenuItem;
import project.coffeeshop.menu.cart.Order;

import java.util.Set;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "users", indexes = {
        @Index(name = "i_user_username", columnList = "username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "username", nullable = false)
    private String username;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_menu_item",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id"))
    private Set<MenuItem> favorites;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Order> orders;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addToFavorites(MenuItem menuItem) {
        favorites.add(menuItem);
    }

    public void removeFromFavorites(MenuItem menuItem) {
        favorites.remove(menuItem);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }
}
