package dev.scaraz.mars.core.domain.credential;

import lombok.*;

import javax.persistence.*;
import java.util.Locale;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user_setting")
public class UserSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "ref_user_id")
    private User user;

    @Column(name = "language")
    private Locale lang = Locale.ENGLISH;

}
