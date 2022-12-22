package dev.scaraz.mars.core.domain.credential;

import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_roles")
public class UserRoles extends AuditableEntity {

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
