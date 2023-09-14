package dev.scaraz.mars.app.administration.domain.db;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user_registration")
public class UserRegistration extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    private String name;

    @Column
    private String nik;

    @Column
    private String phone;

    @Column
    private Witel witel;

    @Column
    private String sto;

}
