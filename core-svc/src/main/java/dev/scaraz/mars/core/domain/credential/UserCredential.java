package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_user_credential")
public class UserCredential extends AuditableEntity implements CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ref_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @Override
    public void eraseCredentials() {
        this.setPassword(null);
    }
}
