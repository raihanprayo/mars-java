package dev.scaraz.mars.core.domain.credential;

import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user_credential")
public class UserCredential extends AuditableEntity implements CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "ref_user_id")
    private User user;

    @Column
    private String username;

    @Column
    private String password;

    @Override
    public void eraseCredentials() {
        this.setPassword(null);
    }
}
