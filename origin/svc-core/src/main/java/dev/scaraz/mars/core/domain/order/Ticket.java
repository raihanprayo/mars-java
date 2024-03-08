package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Builder
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

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "ticket")
    private TicketAsset assets;

    @JsonIgnore
    @Builder.Default
    @ToString.Exclude
    @OrderBy("id asc")
    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER)
    private Set<LogTicket> logs = new HashSet<>();

    public boolean isGaul() {
        return gaul > 0;
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        if (sto != null)
            this.sto = sto.toUpperCase();
    }


}
