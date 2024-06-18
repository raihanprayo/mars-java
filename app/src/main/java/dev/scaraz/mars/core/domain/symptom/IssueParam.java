package dev.scaraz.mars.core.domain.symptom;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_issue_param")
public class IssueParam extends AuditableEntity {

    public enum Type {
        NOTE,
        CAPTURE,
        FILE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private boolean required;

    @Column(name = "display_name")
    private String display;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_issue_id")
    private Issue issue;

}
