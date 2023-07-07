package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.InstantDeserializer;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_log_ticket")
@EntityListeners(AuditingEntityListener.class)
public class LogTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_ticket_no", updatable = false, referencedColumnName = "no")
    private Ticket ticket;

    @Column
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_status")
    private TcStatus prev;

    @Enumerated(EnumType.STRING)
    @Column(name = "curr_status")
    private TcStatus curr;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "ref_agent_id")
//    private Agent agent;
    @Column(name = "ref_agent_id")
    private String agentId;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant createdAt;

}
