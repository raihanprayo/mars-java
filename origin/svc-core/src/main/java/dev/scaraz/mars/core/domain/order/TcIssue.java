package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.tools.enums.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class TcIssue {

    @Column(name = "iss_id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "iss_product")
    private Product product;

    @Column(name = "iss_name")
    private String name;

    @Column(name = "iss_desc")
    private String description;

    @Column(name = "iss_score")
    private double score;

    public static TcIssue from(Issue issue) {
        return builder()
                .id(issue.getId())
                .name(issue.getName())
                .description(issue.getDescription())
                .product(issue.getProduct())
                .score(issue.getScore())
                .build();
    }

}
