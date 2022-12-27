package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @Column(updatable = false)
    @GeneratedValue(generator = "ticket-no")
    @GenericGenerator(
            name = "ticket-no",
            strategy = "dev.scaraz.mars.core.util.generator.TicketNoGenerator")
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
    private TcStatus status;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_issue_id", updatable = false)
    private Issue issue;

    @Builder.Default
    @OneToMany(mappedBy = "ticket")
    private Set<TicketAgent> agents = new HashSet<>();

    public boolean isGaul() {
        return gaul > 0;
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.sto = sto.toUpperCase();
    }


}
