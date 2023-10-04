package dev.scaraz.mars.app.witel.domain.manage;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_inbox_layer")
public class InboxLayer extends AuditableEntity {

    @Id
    private String id;
    private Witel witel;
    private int order;
    private String name;
}
