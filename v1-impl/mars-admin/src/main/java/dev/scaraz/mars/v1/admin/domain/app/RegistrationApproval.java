package dev.scaraz.mars.v1.admin.domain.app;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_reg_approval")
public class RegistrationApproval extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
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

    @Column
    private long telegram;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RegistrationApproval)) return false;

        RegistrationApproval that = (RegistrationApproval) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getId(), that.getId()).append(getNo(), that.getNo()).append(getStatus(), that.getStatus()).append(getName(), that.getName()).append(getNik(), that.getNik()).append(getPhone(), that.getPhone()).append(getWitel(), that.getWitel()).append(getSto(), that.getSto()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getId()).append(getNo()).append(getStatus()).append(getName()).append(getNik()).append(getPhone()).append(getWitel()).append(getSto()).toHashCode();
    }

}
