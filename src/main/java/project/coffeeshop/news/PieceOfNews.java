package project.coffeeshop.news;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PieceOfNews {
    private long id;
    private String title;
    private LocalDateTime creationDate;
    private String image;
    private String body;
}
