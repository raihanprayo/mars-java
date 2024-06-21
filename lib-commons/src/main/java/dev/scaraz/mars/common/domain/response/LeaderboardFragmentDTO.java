package dev.scaraz.mars.common.domain.response;

import dev.scaraz.mars.common.tools.enums.Product;
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
public class LeaderboardFragmentDTO {

    private long id;

    private String ticketId;

    private String ticketNo;

    private Long solutionId;
    private String solutionName;

    private String issueId;
    private String issueName;

    private Product issueProduct;

    private TcStatus takeStatus;

    private TcStatus closeStatus;

    // Requestor
    private String rqId;
    private String rqNik;
    private String rqName;

    // Agent
    private String agId;
    private String agNik;
    private String agName;

    private boolean lastTicketWork;

    private boolean lastAgentWork;

    private double score;

    private double scoreResponse;

    private Duration durationResponse;

    private double scoreAction;

    private Duration durationAction;

    private Instant tcCreatedAt;

    private String createdBy;
    private Instant createdAt;

    private String updatedBy;
    private Instant updatedAt;

}
