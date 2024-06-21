package dev.scaraz.mars.core.domain.agent;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@ToString

@Entity
@Table(name = "leaderboard_frg")
public class Leaderboard implements Serializable {

    @Id
    private long id;

    @Column(name = "tc_id")
    private String ticketId;

    @Column(name = "tc_no")
    private String ticketNo;

    @Column(name = "sol_id")
    private Long solutionId;
    @Column(name = "sol_name")
    private String solutionName;

    @Column(name = "iss_id")
    private String issueId;
    @Column(name = "iss_name")
    private String issueName;
    @Column(name = "iss_product")
    @Enumerated(EnumType.STRING)
    private Product issueProduct;

    @Column(name = "st_take")
    @Enumerated(EnumType.STRING)
    private TcStatus takeStatus;
    @Column(name = "st_end")
    @Enumerated(EnumType.STRING)
    private TcStatus closeStatus;

    // Requestor
    @Column(name = "rq_id")
    private String rqId;
    @Column(name = "rq_nik")
    private String rqNik;
    @Column(name = "rq_name")
    private String rqName;

    // Agent
    @Column(name = "ag_id")
    private String agId;
    @Column(name = "ag_nik")
    private String agNik;
    @Column(name = "ag_name")
    private String agName;

    @Column(name = "last_tc_wl")
    private boolean lastTicketWork;

    @Column(name = "last_ag_wl")
    private boolean lastAgentWork;

    @Column(name = "score")
    private double score;

    @Column(name = "drt_response")
    @Type(PostgreSQLIntervalType.class)
    private Duration durationResponse;

    @Column(name = "drt_act")
    @Type(PostgreSQLIntervalType.class)
    private Duration durationAction;

    @Column(name = "tc_created_at")
    private Instant tcCreatedAt;

    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Leaderboard that)) return false;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(isLastTicketWork(), that.isLastTicketWork())
                .append(isLastAgentWork(), that.isLastAgentWork())
                .append(getScore(), that.getScore())
                .append(getTicketId(), that.getTicketId())
                .append(getTicketNo(), that.getTicketNo())
                .append(getSolutionId(), that.getSolutionId())
                .append(getSolutionName(), that.getSolutionName())
                .append(getIssueId(), that.getIssueId())
                .append(getIssueName(), that.getIssueName())
                .append(getIssueProduct(), that.getIssueProduct())
                .append(getTakeStatus(), that.getTakeStatus())
                .append(getCloseStatus(), that.getCloseStatus())
                .append(getRqId(), that.getRqId())
                .append(getRqNik(), that.getRqNik())
                .append(getRqName(), that.getRqName())
                .append(getAgId(), that.getAgId())
                .append(getAgNik(), that.getAgNik())
                .append(getAgName(), that.getAgName())
                .append(getDurationResponse(), that.getDurationResponse())
                .append(getDurationAction(), that.getDurationAction())
                .append(getTcCreatedAt(), that.getTcCreatedAt())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getTicketId())
                .append(getTicketNo())
                .append(getSolutionId())
                .append(getSolutionName())
                .append(getIssueId())
                .append(getIssueName())
                .append(getIssueProduct())
                .append(getTakeStatus())
                .append(getCloseStatus())
                .append(getRqId())
                .append(getRqNik())
                .append(getRqName())
                .append(getAgId())
                .append(getAgNik())
                .append(getAgName())
                .append(isLastTicketWork())
                .append(isLastAgentWork())
                .append(getScore())
                .append(getDurationResponse())
                .append(getDurationAction())
                .append(getTcCreatedAt())
                .toHashCode();
    }

}
