package project.coffeeshop.menu.cart;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Order {
    private long id;
    private LocalDateTime creationTime;
    private LocalDateTime readyTime;
    private double price;

    public Order(LocalDateTime creationTime, LocalDateTime readyTime, double price) {
        this.creationTime = creationTime;
        this.readyTime = readyTime;
        this.price = price;
    }
}