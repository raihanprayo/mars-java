package dev.scaraz.mars.core.domain.credential;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user_approval")
public class AccountApproval extends TimestampEntity {

    public static String WAIT_APPROVAL = "WAIT_APPROVAL";
    public static String REQUIRE_DOCUMENT = "REQUIRE_DOCUMENT";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(updatable = false)
    private String no;

    @Column
    private String status;

    @Column
    private String name;

    @Column
    private String nik;

    @Column
    private String phone;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column
    private String sto;

    @Embedded
    @Builder.Default
    private AccountTg tg = new AccountTg();

}
