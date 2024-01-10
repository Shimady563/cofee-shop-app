package project.coffeeshop.menu;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MenuItem {
    private long id;
    private String name;
    private int price;
    private int volume;
    private String image;
}
