package dev.scaraz.mars.user.datasource.domain;

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
