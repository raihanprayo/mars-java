package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Locale;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_user_setting")
public class UserSetting extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ref_user_id")
    private User user;

    @Column(name = "language")
    @Builder.Default
    private Locale lang = Locale.ENGLISH;

}
