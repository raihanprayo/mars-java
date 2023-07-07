package dev.scaraz.mars.v1.core.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_group_members")
@EntityListeners(AuditingEntityListener.class)
public class GroupMember extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ref_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ref_group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "ref_role_id")
    private Role role;

}
