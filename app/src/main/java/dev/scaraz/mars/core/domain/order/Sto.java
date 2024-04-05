package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_sto")
public class Sto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column
    @Enumerated(EnumType.STRING)
    public Witel witel;

    @Column
    public String datel;

    @Column
    public String alias;

    @Column
    public String name;

}
