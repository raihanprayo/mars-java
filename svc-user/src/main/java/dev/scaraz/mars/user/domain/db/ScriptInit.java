package dev.scaraz.mars.user.domain.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_script_init")
public class ScriptInit {

    @Id
    private String id;

    @Column
    private boolean executed;

    @Column(name = "message")
    private String errorMessage;

}
