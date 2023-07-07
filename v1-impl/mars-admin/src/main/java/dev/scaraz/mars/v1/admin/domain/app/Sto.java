package dev.scaraz.mars.v1.admin.domain.app;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_sto")
public class Sto extends AuditableEntity {

    @Id
    private String code;

    @Column
    private String name;

    @Column
    private Witel witel;

    @Column
    private String datel;

}
