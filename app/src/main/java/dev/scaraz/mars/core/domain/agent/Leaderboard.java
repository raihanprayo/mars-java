package dev.scaraz.mars.core.domain.agent;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

    //    @Column(name = "score_response")
    @Transient
    private double scoreResponse;
    @Column(name = "drt_response")
    @Type(PostgreSQLIntervalType.class)
    private Duration durationResponse;

    //    @Column(name = "score_act")
    @Transient
    private double scoreAction;
    @Column(name = "drt_act")
    @Type(PostgreSQLIntervalType.class)
    private Duration durationAction;

    @Column(name = "tc_created_at")
    private Instant tcCreatedAt;

//    @Column(name = "created_by")
//    private String createdBy;
//    @Column(name = "created_at")
//    private Instant createdAt;
//
//    @Column(name = "updated_by")
//    private String updatedBy;
//    @Column(name = "updated_at")
//    private Instant updatedAt;

}
