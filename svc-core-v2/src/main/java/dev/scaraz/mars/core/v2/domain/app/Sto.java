package dev.scaraz.mars.core.v2.domain.app;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_sto")
public class Sto extends AuditableEntity {

    @Id
    @Column(name = "code")
    public String id;

    @Column
    public String name;

    @Column
    @Enumerated(EnumType.STRING)
    public Witel witel;

    @Column
    public String datel;

}
