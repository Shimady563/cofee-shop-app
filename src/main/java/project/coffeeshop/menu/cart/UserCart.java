package project.coffeeshop.menu.cart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import project.coffeeshop.authentication.User;
import project.coffeeshop.menu.MenuItem;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_cart")
public class UserCart {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Setter
    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    public UserCart(User user, MenuItem menuItem) {
        this.user = user;
        this.menuItem = menuItem;
    }
}
