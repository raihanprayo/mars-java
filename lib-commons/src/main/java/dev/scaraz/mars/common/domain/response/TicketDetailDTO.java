package dev.scaraz.mars.common.domain.response;

import dev.scaraz.mars.common.tools.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailDTO implements Serializable {

    private String id;

    private String no;

    private Witel witel;

    private String sto;

    private String incidentNo;

    private String serviceNo;

    private TcStatus status;

    private TcSource source;

    private String senderName;

    private long senderId;

    private String note;

    private boolean gaul;

    private int gaulCount = 0;

    private IssueDTO issue;

    private Product product;

    private int agentCount;

    private boolean wip;

    private Long wipId;

    private AgStatus wipStatus;

    private String wipBy;

    private TicketAgeDTO age;

}
