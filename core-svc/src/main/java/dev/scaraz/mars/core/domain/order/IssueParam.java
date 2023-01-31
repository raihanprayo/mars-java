package dev.scaraz.mars.core.domain.order;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_issue_param")
public class IssueParam extends AuditableEntity {

    public enum Type {
        NOTE,
        CAPTURE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private boolean required;

    @Column(name = "display_name")
    private String display;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_issue_id")
    private Issue issue;

}
