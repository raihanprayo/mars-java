package dev.scaraz.mars.core.domain.credential;

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

    @ManyToOne
    @JoinColumn(name = "parent_group_id")
    private Group parent;

    @Builder.Default
    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GroupSetting setting = new GroupSetting();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "t_group_members",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_group_id"))
    private Set<User> members = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }
    public void addRoles(Role... roles) {
        this.roles.addAll(Set.of(roles));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(group))
                .append(getId(), group.getId())
                .append(getName(), group.getName())
                .append(getParent(), group.getParent())
                .append(getSetting(), group.getSetting())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getName())
                .append(getParent())
                .append(getSetting())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parent=" + Optional.ofNullable(parent).map(Group::getId).orElse(null) +
                ", setting=" + setting +
                '}';
    }

    @PreUpdate
    @PrePersist
    protected void prePersist() {
        if (this.setting.getGroup() == null)
            this.setting.setGroup(this);

        for (Role role : roles) {
            if (role.getGroup() == null)
                role.setGroup(this);
        }
    }

}
