package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.utils.AppConstants;
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
    @Setter(AccessLevel.NONE)
    @Column(name = "no", unique = true)
    private String value;

    @NotNull
    @Column
    private String status;

    @Column(name = "ref_issue_id")
    private Long issueId;

    @Builder.Default
    private long ttl = -1;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @CreatedBy
    @Setter(AccessLevel.NONE)
    private String createdBy;

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = String.valueOf(value);
    }

    public void setValue(int value) {
        this.value = String.valueOf(value);
    }

    public void setValue(boolean value) {
        this.value = String.valueOf(value);
    }

    public String getCacheKey() {
        return AppConstants.Cache.j(AppConstants.Cache.TC_CONFIRM_NS, String.valueOf(this.id));
    }

    public static final String
            CLOSED = "CLOSED",
            PENDING = "PENDING",
            POST_PENDING = "POST_PENDING",
            POST_PENDING_CONFIRMATION = "POST_PENDING_CONFIRM";

    public static final String
            INSTANT_START = "INSTANT_START",
            INSTANT_NETWORK = "INSTANT_NETWORK",
            INSTANT_PARAM = "INSTANT_PARAM",
            INSTANT_FORM = "INSTANT_FORM";
}
