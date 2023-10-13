package dev.scaraz.mars.core.domain.credential;

import javax.persistence.*;

@Entity
@Table(name = "t_role_composite")
public class RoleComposite {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Role parent;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Role child;

}
