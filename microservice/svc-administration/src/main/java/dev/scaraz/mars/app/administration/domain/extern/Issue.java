package dev.scaraz.mars.app.administration.domain.extern;


import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

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
