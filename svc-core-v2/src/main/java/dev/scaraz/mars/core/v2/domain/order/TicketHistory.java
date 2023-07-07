package dev.scaraz.mars.core.v2.domain.order;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ticket_history")
@EntityListeners(AuditingEntityListener.class)
public class TicketHistory {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "ticket_no")
    private String ticket;

    @Column
    private TcStatus status;

    @Column
    private String message;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TicketHistory)) return false;

        TicketHistory that = (TicketHistory) o;

        return new EqualsBuilder().append(getId(), that.getId()).append(getTicket(), that.getTicket()).append(getStatus(), that.getStatus()).append(getMessage(), that.getMessage()).append(getCreatedBy(), that.getCreatedBy()).append(getCreatedAt(), that.getCreatedAt()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getTicket()).append(getStatus()).append(getMessage()).append(getCreatedBy()).append(getCreatedAt()).toHashCode();
    }
}
