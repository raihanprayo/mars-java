package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.security.credential.CredentialStructure;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_account_credential")
public class AccountCredential extends TimestampEntity implements CredentialStructure {

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
        Object[] order = {algorithm, secret, hashIteration, password};
        return Stream.of(order)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(":::"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AccountCredential)) return false;

        AccountCredential that = (AccountCredential) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getId(), that.getId())
                .append(getPriority(), that.getPriority())
                .append(getAccount(), that.getAccount())
                .append(getAlgorithm(), that.getAlgorithm())
                .append(getSecret(), that.getSecret())
                .append(getHashIteration(), that.getHashIteration())
                .append(getPassword(), that.getPassword())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getPriority())
                .append(getAccount())
                .toHashCode();
    }
}
