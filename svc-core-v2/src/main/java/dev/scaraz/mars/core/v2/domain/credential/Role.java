package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_role")
public class Role extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private int id;

    @Column
    private String name;

}
