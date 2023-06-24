package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_account")
public class Account extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    private String username;

    @Column
    private String name;

    @Email
    @Column
    private String email;

    @Column
    private Witel witel;

    @Column
    private String sto;

    @Column
    private boolean active;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_account_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @Column(name = "expired_at")
    private Instant expiredAt;

}
