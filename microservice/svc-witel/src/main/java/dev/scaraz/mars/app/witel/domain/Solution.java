package dev.scaraz.mars.app.witel.domain;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_solution")
public class Solution extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private Product product;

}
