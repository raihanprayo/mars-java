package dev.scaraz.mars.core.domain.order;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.*;
import org.hibernate.annotations.*;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name="deletedFilter", condition = "deleted = :isDeleted")
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
    @Builder.Default
    private BigDecimal score = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private boolean deleted = false;

}
