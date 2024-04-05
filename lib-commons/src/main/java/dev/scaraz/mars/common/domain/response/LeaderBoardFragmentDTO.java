package dev.scaraz.mars.common.domain.response;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardFragmentDTO {

    private long id;

    private String ticketId;

    private long workspaceId;

    private TcStatus start;

    private TcStatus close;

    private Duration actionDuration;

    private Duration responseDuration;

    private IssueDTO issue;

    private SolutionDTO solution;

    private Instant lastTicketLogAt;

    private String createdBy;
    private Instant createdAt;

    private String updatedBy;
    private Instant updatedAt;

}
