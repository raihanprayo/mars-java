package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import lombok.*;

import java.time.Instant;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorkspaceDTO {

    private long id;

    private AgStatus status;

    private AgentDTO agent;

    @ToString.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<AgentWorklogDTO> worklogs;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant updatedAt;

}
