package dev.scaraz.mars.v1.core.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_agent")
public class Agent extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "nik")
    private String nik;

    @Column(name = "tg_id")
    private Long telegramId;

    @Column(name = "ref_user_id", updatable = false)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Agent)) return false;

        Agent agent = (Agent) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getId(), agent.getId())
                .append(getNik(), agent.getNik())
                .append(getTelegramId(), agent.getTelegramId())
                .append(getUserId(), agent.getUserId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getNik())
                .append(getTelegramId())
                .append(getUserId())
                .toHashCode();
    }
}
