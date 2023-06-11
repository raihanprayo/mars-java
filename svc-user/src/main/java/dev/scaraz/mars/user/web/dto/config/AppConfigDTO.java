package dev.scaraz.mars.user.web.dto.config;

import dev.scaraz.mars.user.domain.db.AppConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfigDTO<T> implements Serializable {

    protected AppConfig.Type type;

    protected String name;

    protected String title;

    protected String description;

    protected T value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AppConfigDTO)) return false;

        AppConfigDTO<?> that = (AppConfigDTO<?>) o;

        return new EqualsBuilder()
                .append(getType(), that.getType())
                .append(getName(), that.getName())
                .append(getTitle(), that.getTitle())
                .append(getDescription(), that.getDescription())
                .append(getValue(), that.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getType())
                .append(getName())
                .append(getTitle())
                .append(getDescription())
                .append(getValue())
                .toHashCode();
    }
}
