package dev.scaraz.mars.core.domain.order;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_issue")
public class Issue extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Product product;

    @Column
    private String name;

    @Column(name = "display_name")
    private String alias;

    /**
     * Instruksi
     */
    @Column
    private String description;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "issue", fetch = FetchType.EAGER)
    private List<IssueParam> params = new ArrayList<>();

}
