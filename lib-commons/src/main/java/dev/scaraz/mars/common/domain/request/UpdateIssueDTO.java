package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIssueDTO {

    private String name;

    private Product product;

    private BigDecimal score;

    private String alias;

    private String description;

    @Builder.Default
    private List<IssueParamDTO> params = new ArrayList<>();

    @Builder.Default
    private List<Long> deletedParams = new ArrayList<>();

}
