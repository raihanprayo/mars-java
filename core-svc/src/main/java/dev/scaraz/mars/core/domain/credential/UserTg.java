package dev.scaraz.mars.core.domain.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class UserTg implements Serializable {

    @Column(name = "tg_id")
    private Long id;

    @Column(name = "tg_username")
    private String username;

}
