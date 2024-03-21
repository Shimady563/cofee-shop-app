package project.coffeeshop.menu.cart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.coffeeshop.authentication.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "ready_time", nullable = false)
    private LocalDateTime readyTime;

    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    public Order(LocalDateTime creationTime, LocalDateTime readyTime, double price) {
        this.creationTime = creationTime;
        this.readyTime = readyTime;
        this.price = price;
    }
}

//@AllArgsConstructor
//@NoArgsConstructor
//@Setter
//@Getter
//@ToString
//public class Order {
//    private long id;
//    private LocalDateTime creationTime;
//    private LocalDateTime readyTime;
//    private double price;
//
//    public Order(LocalDateTime creationTime, LocalDateTime readyTime, double price) {
//        this.creationTime = creationTime;
//        this.readyTime = readyTime;
//        this.price = price;
//    }
//}