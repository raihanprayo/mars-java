package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.tools.enums.Product;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Getter
@ToString

@Entity
@Table(name = "v_leader_board_fragment")
public class LeaderBoardFragment {

    @Id
    private String id;

    @Column(name = "ref_agent_id")
    private String agentId;

    @Column(name = "ref_ticket_no")
    private String ticketNo;

    @Column(name = "ref_user_id")
    private String userId;

    @Column(name = "issue_name")
    private String issueName;

    @Column(name = "issue_product")
    @Enumerated(EnumType.STRING)
    private Product issueProduct;

    @Column(name = "avg_respon")
    private long avgRespon;

    @Column(name = "avg_respon_total")
    private int avgResponTotal;

    @Column(name = "avg_action")
    private long avgAction;

    @Column(name = "avg_action_total")
    private int avgActionTotal;

    @Column(name = "total_dispatch")
    private int totalDispatch;

    @Column(name = "total_handle_dispatch")
    private int totalHandleDispatch;

    @Column(name = "ws_created_at")
    private Instant workspaceCreatedAt;

    @Column(name = "ws_updated_at")
    private Instant workspaceUpdatedAt;

    @Column(name = "tc_created_at")
    private Instant ticketCreatedAt;

    @Column(name = "tc_updated_at")
    private Instant ticketUpdatedAt;

}
