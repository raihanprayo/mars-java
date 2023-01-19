package dev.scaraz.mars.core.domain.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class UserTg {

    @Column(name = "tg_id")
    private Long id;

    @Column(name = "tg_username")
    private String username;

}
