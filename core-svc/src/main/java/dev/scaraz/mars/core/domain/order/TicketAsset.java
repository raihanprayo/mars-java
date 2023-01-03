package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.scaraz.mars.common.domain.TimestampEntity;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@JsonIncludeProperties({"paths"})
@Entity
@Table(name = "t_ticket_asset")
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class TicketAsset extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "ref_ticket_id")
    private Ticket ticket;

    @OneToOne
    @JoinColumn(name = "ref_agent_id")
    private TicketAgent agent;

    @Column
    @Builder.Default
    @Setter(AccessLevel.NONE)
    @Type(type = "string-array")
    private String[] paths = new String[0];

    public void addPath(String path) {
        ArrayList<String> p = new ArrayList<>(List.of(this.paths));
        p.add(path);
        this.paths = p.toArray(new String[0]);
    }

    public void addPaths(String... paths) {
        ArrayList<String> p = new ArrayList<>(List.of(this.paths));
        p.addAll(Arrays.asList(paths));
        this.paths = p.toArray(new String[0]);
    }

    public void removePath(String path) {
        ArrayList<String> p = new ArrayList<>(List.of(this.paths));
        p.remove(path);
        this.paths = p.toArray(new String[0]);
    }

    public void removePaths(String... paths) {
        ArrayList<String> p = new ArrayList<>(List.of(this.paths));
        p.removeAll(Set.of(paths));
        this.paths = p.toArray(new String[0]);
    }

    public int size() {
        return paths.length;
    }

}
