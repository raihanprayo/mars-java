package dev.scaraz.mars.core.v2.domain.app;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_config_tag")
@EntityListeners(AuditingEntityListener.class)
public class ConfigTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(updatable = false)
    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ConfigTag)) return false;

        ConfigTag configTag = (ConfigTag) o;

        return new EqualsBuilder()
                .append(getId(), configTag.getId())
                .append(getName(), configTag.getName())
                .append(getCreatedAt(), configTag.getCreatedAt())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getCreatedAt())
                .toHashCode();
    }
}
