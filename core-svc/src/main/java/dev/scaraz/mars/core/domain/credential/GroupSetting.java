package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_group_setting")
public class GroupSetting extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ref_group_id", updatable = false)
    private Group group;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "can_login")
    private boolean canLogin;

    public boolean canLogin() {
        return this.canLogin;
    }

    public void canLogin(boolean canLogin) {
        this.canLogin = canLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof GroupSetting)) return false;

        GroupSetting that = (GroupSetting) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getId(), that.getId())
                .append(canLogin, that.canLogin)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(canLogin)
                .toHashCode();
    }
}
