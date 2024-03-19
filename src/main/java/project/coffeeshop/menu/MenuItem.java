package project.coffeeshop.menu;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.coffeeshop.authentication.User;

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

    @ManyToMany(mappedBy = "menuItems", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_menu_item",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;
}

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//public class MenuItem {
//    private long id;
//    private String name;
//    private double price;
//    private int volume;
//    private String image;
//}
