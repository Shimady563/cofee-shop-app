package project.coffeeshop.authentication;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    private UUID id;

    @Column(name = "expiration_time", nullable = false)
    @Setter
    private LocalDateTime expirationTime;

    @OneToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
