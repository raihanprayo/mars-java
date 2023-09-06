package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIssueDTO {

    private String name;

    @Builder.Default
    private BigDecimal score = BigDecimal.ZERO;

    private Product product;

    private String alias;

    private String description;

    @Builder.Default
    private List<IssueParamDTO> params = new ArrayList<>();

    @Builder.Default
    private List<Long> deletedParams = new ArrayList<>();

}
