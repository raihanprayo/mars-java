package dev.scaraz.mars.app.witel.domain;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_asset")
public class Asset extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    @Column(name = "content_type")
    private String contentType;

    @Column
    private byte[] content;

}
