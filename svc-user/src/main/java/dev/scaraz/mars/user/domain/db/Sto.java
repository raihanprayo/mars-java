package dev.scaraz.mars.user.domain.db;

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
public class Sto extends TimestampEntity {

    @Id
    @Column(updatable = false)
    public String id;

    @Column
    @Enumerated(EnumType.STRING)
    public Witel witel;

    @Column
    public String datel;

    @Column
    public String name;
}
