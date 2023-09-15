package dev.scaraz.mars.app.witel.domain;

import dev.scaraz.mars.common.domain.TimestampEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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
