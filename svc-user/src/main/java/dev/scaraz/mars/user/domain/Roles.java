package dev.scaraz.mars.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_roles")
public class Roles extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ref_role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "ref_user_id")
    private MarsUser user;

    public static Roles of(Role role, MarsUser user) {
        return new Roles(0, role, user);
    }

}
