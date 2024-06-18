package dev.scaraz.mars.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity extends TimestampEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AuditableEntity)) return false;

        AuditableEntity that = (AuditableEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getCreatedBy(), that.getCreatedBy())
                .append(getUpdatedBy(), that.getUpdatedBy())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getCreatedBy())
                .append(getUpdatedBy())
                .toHashCode();
    }
}
