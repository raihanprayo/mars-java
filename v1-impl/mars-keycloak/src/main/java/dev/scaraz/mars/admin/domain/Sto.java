package dev.scaraz.mars.admin.domain;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_sto")
public class Sto {

    @Id
    public String code;

    @Column
    public String name;

    @Column
    @Enumerated(EnumType.STRING)
    public Witel witel;

    @Column
    public String datel;

}
