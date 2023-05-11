package dev.scaraz.mars.core.datasource.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket")
public class Ticket extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "no", updatable = false)
    private String no;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column(updatable = false)
    private String sto;

    @Column(name = "incident_no", updatable = false)
    private String incidentNo;

    @Column(name = "service_no", updatable = false)
    private String serviceNo;

    @Column
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TcStatus status = TcStatus.OPEN;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private TcSource source;

    @Column(updatable = false)
    private String senderName;

    @Column(updatable = false)
    private long senderId;

    @Column
    private String note;

    @Builder.Default
    @Column(updatable = false)
    private int gaul = 0;

    @Column(name = "con_message_id")
    private Long confirmMessageId;

    @Column(name = "con_pending_message_id")
    private Long confirmPendingMessageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_issue_id", updatable = false)
    private Issue issue;

    @JsonIgnore
    @Builder.Default
    @ToString.Exclude
    @OrderBy("id asc")
    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private Set<LogTicket> logs = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_ticket_asset",
            schema = "mars",
            joinColumns = @JoinColumn(name = "ref_ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_asset_id"))
    private Set<Asset> assets = new HashSet<>();

    public boolean isGaul() {
        return gaul > 0;
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        if (sto != null)
            this.sto = sto.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Ticket)) return false;

        Ticket ticket = (Ticket) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getSenderId(), ticket.getSenderId())
                .append(isGaul(), ticket.isGaul())
                .append(getId(), ticket.getId())
                .append(getNo(), ticket.getNo())
                .append(getWitel(), ticket.getWitel())
                .append(getSto(), ticket.getSto())
                .append(getIncidentNo(), ticket.getIncidentNo())
                .append(getServiceNo(), ticket.getServiceNo())
                .append(getStatus(), ticket.getStatus())
                .append(getSource(), ticket.getSource())
                .append(getSenderName(), ticket.getSenderName())
                .append(getNote(), ticket.getNote())
                .append(getConfirmMessageId(), ticket.getConfirmMessageId())
                .append(getConfirmPendingMessageId(), ticket.getConfirmPendingMessageId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getNo())
                .append(getWitel())
                .append(getSto())
                .append(getIncidentNo())
                .append(getServiceNo())
                .append(getStatus())
                .append(getSource())
                .append(getSenderName())
                .append(getSenderId())
                .append(getNote())
                .append(isGaul())
                .append(getConfirmMessageId())
                .append(getConfirmPendingMessageId())
                .toHashCode();
    }
}
