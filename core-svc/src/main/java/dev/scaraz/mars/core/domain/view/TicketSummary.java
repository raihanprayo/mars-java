package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.credential.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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

    @Column(name = "ref_issue_id")
    private String issueId;

    @Column
    private Product product;

    @Column(name = "agent_count")
    private int agentCount;

    @Column
    private boolean wip;

    @ManyToOne
    @JoinColumn(name = "wip_by")
    private User wipBy;

    @Column
    @Type(type = "string-array")
    private String[] assets;

}
