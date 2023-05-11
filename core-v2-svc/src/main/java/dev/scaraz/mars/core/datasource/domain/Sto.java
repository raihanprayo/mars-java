package dev.scaraz.mars.core.datasource.domain;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_sto")
public class Sto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Witel witel;

    @Column
    private String datel;

    @Column
    private String alias;

    @Column
    private String name;

}
