package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {

    private String id;

    private String nik;

    private Long telegramId;

    private String userId;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;

    private String createdBy;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant updatedAt;

    private String updatedBy;

}
