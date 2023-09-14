package dev.scaraz.mars.app.api.domain.order;

import dev.scaraz.mars.app.api.domain.Issue;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket")
public class Ticket extends AuditableEntity {

    @Id
    private String id;

    @Column
    private String no;

    @Column
    @Enumerated(EnumType.STRING)
    private TcStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private TcSource source;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column
    private String sto;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

}
