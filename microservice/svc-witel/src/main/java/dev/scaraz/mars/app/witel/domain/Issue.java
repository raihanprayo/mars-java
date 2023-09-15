package dev.scaraz.mars.app.witel.domain;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.math.BigDecimal;
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
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class Issue extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column
    @Enumerated(EnumType.STRING)
    private Product product;

    @Column
    private String code;

    @Column
    private String name;

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
    private BigDecimal score;

    @Column
    @Builder.Default
    private boolean ignoreGaul = true;

    @Column
    @Builder.Default
    private boolean deleted = false;

}
