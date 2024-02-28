package project.coffeeshop.menu.cart;

import lombok.*;
import project.coffeeshop.menu.MenuItem;

@Getter
@Setter
@ToString
public class CartItem extends MenuItem {
    int quantity;

    public CartItem(long id, String name, double price, int volume, String image, int quantity) {
        super(id, name, price, volume, image);
        this.quantity = quantity;
    }

    public CartItem() {
    }
}
