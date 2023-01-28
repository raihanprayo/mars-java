package dev.scaraz.mars.core.domain.order;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket_confirm")
@EntityListeners(AuditingEntityListener.class)
public class TicketConfirm {

    @Id
    private long id;

    @NotNull
    @Column(unique = true)
    private String no;

    @NotNull
    private String status;

    @Builder.Default
    private long ttl = -1;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @CreatedBy
    @Setter(AccessLevel.NONE)
    private String createdBy;

    public static final String
            CLOSED = "CLOSED",
            PENDING = "PENDING",
            POST_PENDING = "POST_PENDING",
            POST_PENDING_CONFIRMATION = "POST_PENDING_CONFIRM";
}
