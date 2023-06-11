package dev.scaraz.mars.user.domain;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_app_config_category")
public class AppConfigCategory extends TimestampEntity {

    @Id
    private String id;

    @Builder.Default
    @OrderBy("id DESC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "category")
    private Set<AppConfig> configs = new HashSet<>();

}
