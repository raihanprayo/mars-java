package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_account_credential")
public class AccountCredential extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "algo")
    private String algorithm;

    @Column(name = "secret")
    private String secret;

    @Column(name = "hash_iteration")
    private Integer hashIteration;

    @Column
    private String password;

    public String format() {
        return List.of(algorithm, secret, hashIteration, password).stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(":::"));
    }
}
