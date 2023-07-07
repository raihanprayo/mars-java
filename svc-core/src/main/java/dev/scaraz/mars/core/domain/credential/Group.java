package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_group")
public class Group extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    private String name;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    private Group parent;

    @Builder.Default
    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GroupSetting setting = new GroupSetting();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(group))
                .append(getId(), group.getId())
                .append(getName(), group.getName())
                .append(getSetting(), group.getSetting())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getName())
                .append(getSetting())
                .toHashCode();
    }

    @PreUpdate
    @PrePersist
    protected void prePersist() {
        if (this.setting.getGroup() == null)
            this.setting.setGroup(this);
    }

}
