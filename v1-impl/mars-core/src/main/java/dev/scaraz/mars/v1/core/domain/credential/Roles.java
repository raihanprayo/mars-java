package dev.scaraz.mars.v1.core.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_roles")
public class Roles extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ref_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ref_role_id")
    private Role role;

}
