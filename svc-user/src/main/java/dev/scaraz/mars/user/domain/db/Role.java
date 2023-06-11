package dev.scaraz.mars.user.domain.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Slf4j
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_role")
public class Role extends AuditableEntity implements GrantedAuthority {

    public Role(String name) {
        this.name = name;
    }

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getId(), role.getId())
                .append(getName(), role.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getName())
                .toHashCode();
    }

    @PrePersist
    private void prePersist() {
        log.trace("Lower case role name");
        this.name = this.name.toLowerCase();
    }

}
