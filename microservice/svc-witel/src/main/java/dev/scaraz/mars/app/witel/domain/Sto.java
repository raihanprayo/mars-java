package dev.scaraz.mars.app.witel.domain;

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
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column
    private String datel;

    @Column
    private String alias;

    @Column
    private String name;

}
