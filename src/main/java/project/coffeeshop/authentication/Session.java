package project.coffeeshop.authentication;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Session {
    private UUID id;
    private long userId;
    private LocalDateTime expirationTime;
}
