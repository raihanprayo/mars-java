package dev.scaraz.mars.core.domain.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.converter.InstantDeserializer;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.order.TcIssue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "v_ticket_summary")
public class TicketSummary extends AuditableEntity {
    @Id
    private String id;

    @Column
    private String no;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column
    private String sto;

    @Column(name = "incident_no", updatable = false)
    private String incidentNo;

    @Column(name = "service_no", updatable = false)
    private String serviceNo;

    @Column
    @Enumerated(EnumType.STRING)
    private TcStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private TcSource source;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_id")
    private long senderId;

    @Column
    private String note;

    @Column(name = "is_gaul")
    private boolean gaul;

    @Column(name = "gaul")
    private int gaulCount = 0;

    //    @ManyToOne
//    @JoinColumn(name = "ref_issue_id")
//    private Issue issue;
    @AttributeOverride(name = "product", column = @Column(name = "product"))
    private TcIssue issue;

    @Column(name = "agent_count")
    private int agentCount;

    @Column
    private boolean wip;

    @Column(name = "wip_id")
    private Long wipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wip_status")
    private AgStatus wipStatus;

    @Column(name = "wip_by")
    private String wipBy;

    @Column
    private Duration age;

    @Column(name = "closed_at")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    private Instant closedAt;

    @Column
    private boolean deleted;

    @Column(name = "deleted_at")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    private Instant deletedAt;

}
