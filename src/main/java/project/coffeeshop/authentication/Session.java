package project.coffeeshop.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class Session {
    private UUID id;
    private long userId;
    private LocalDateTime expirationTime;

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expirationTime);
    }
}
