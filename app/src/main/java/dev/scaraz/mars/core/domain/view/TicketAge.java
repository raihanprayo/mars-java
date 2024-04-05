package dev.scaraz.mars.core.domain.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.DurationDeserializer;
import dev.scaraz.mars.common.tools.converter.DurationSerializer;
import dev.scaraz.mars.common.tools.converter.InstantDeserializer;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class TicketAge {

    @Column(name = "age")
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration age;

    @Nullable
    @Column(name = "age_action")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonProperty(index = Integer.MAX_VALUE - 2)
    private Instant action;

    @Nullable
    @Column(name = "age_response")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonProperty(index = Integer.MAX_VALUE - 2)
    private Instant response;

}
