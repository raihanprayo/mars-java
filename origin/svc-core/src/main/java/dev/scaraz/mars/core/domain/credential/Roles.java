package dev.scaraz.mars.core.domain.credential;

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
    private Account account;

    @ManyToOne
    @JoinColumn(name = "ref_role_id")
    private Role role;

    public Roles(Account account, Role role) {
        this.account = account;
        this.role = role;
    }

}
