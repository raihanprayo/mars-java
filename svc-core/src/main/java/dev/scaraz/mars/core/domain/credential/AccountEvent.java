package dev.scaraz.mars.core.domain.credential;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user_event")
public class AccountEvent {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    private String type;

    @Column
    @Type(type = "jsonb")
    private String details;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

}
