package dev.scaraz.mars.core.domain.symptom;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

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
@SQLDelete(sql = "update t_issue set deleted = true where id=?")
@FilterDef(name = "issue-delete", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name="issue-delete", condition = "deleted = :isDeleted")
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
    @OneToMany(mappedBy = "issue", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<IssueParam> params = new ArrayList<>();

    @Column
    private double score;

    @Column
    @Builder.Default
    private boolean deleted = false;

}
