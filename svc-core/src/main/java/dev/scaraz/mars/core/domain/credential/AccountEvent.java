package dev.scaraz.mars.core.domain.credential;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    private String type;

    @Column
    private String user;

    @Column
    private String details;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

}
