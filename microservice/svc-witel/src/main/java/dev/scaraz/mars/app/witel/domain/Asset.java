package dev.scaraz.mars.app.witel.domain;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.AssetType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset extends TimestampEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private AssetType type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Asset parent;

    @Column(name = "content_type")
    private String contentType;

    @Column
    private byte[] content;

}
