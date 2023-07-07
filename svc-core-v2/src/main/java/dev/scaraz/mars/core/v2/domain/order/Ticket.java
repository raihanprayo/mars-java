package dev.scaraz.mars.core.v2.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashSet;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String no;

    @Enumerated(EnumType.STRING)
    private TcStatus status;
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private TcSource source;
    @Column(updatable = false)
    private int gaul;

    @Column(name = "incident_no", updatable = false)
    private String incidentNo;
    @Column(name = "service_no", updatable = false)
    private String serviceNo;

    @Column(name = "issue_product", updatable = false)
    private Product issueProduct;
    @Column(name = "issue_no", updatable = false)
    private String issueName;


    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private Witel witel;
    @Column(updatable = false)
    private String sto;
    @Column(name = "sender_name", updatable = false)
    private String senderName;
    @Column(name = "sender_tg", updatable = false)
    private Long senderTg;

    @Builder.Default
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER)
    private Set<TicketHistory> histories = new LinkedHashSet<>();

}
