package dev.scaraz.mars.core.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class WlSolution {

    public WlSolution(Solution solution) {
        this.id = solution.getId();
        this.name = solution.getName();
        this.description = solution.getDescription();
    }

    @Column(name = "sol_id")
    private long id;

    @Column(name = "sol_name")
    private String name;

    @Column(name = "sol_desc")
    private String description;

}
