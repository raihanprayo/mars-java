package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_account_expired")
public class AccountExpired extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "account_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column
    private boolean active;

    @Column
    private Instant date;

    public static AccountExpired inactive() {
        return AccountExpired.builder()
                .active(false)
                .date(null)
                .build();
    }

    public static AccountExpired active(Duration duration) {
        Instant next = Instant.now()
                .plus(duration.toMillis(), ChronoUnit.DAYS);
        return AccountExpired.builder()
                .active(true)
                .date(next)
                .build();
    }

    public static AccountExpired pick(boolean expireable, Duration duration) {
        return expireable ? active(duration) : inactive();
    }

}
