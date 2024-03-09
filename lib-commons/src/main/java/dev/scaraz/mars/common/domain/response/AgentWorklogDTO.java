package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorklogDTO implements Serializable {

    private long id;

    private TcStatus takeStatus;

    private TcStatus closeStatus;

    private String message;

    private String reopenMessage;

    private Instant createdAt;

    private Instant updatedAt;

    private SolutionDTO solution;

    @ToString.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AgentWorkspaceDTO workspace;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TicketShortDTO ticket;

}
