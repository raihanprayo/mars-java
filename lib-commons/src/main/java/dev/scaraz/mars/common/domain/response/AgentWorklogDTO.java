package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorklogDTO implements Serializable {

    private long id;

    private TcStatus takeStatus;

    private TcStatus closeStatus;

    private Long solution;

    private String message;

    private String reopenMessage;

    @ToString.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AgentWorkspaceDTO workspace;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant updatedAt;

}
