package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIssueDTO {

    @NotNull
    private String code;

    private String name;

    private Witel witel;

    @NotNull
    private Product product;

    @NotNull
    private String description;

    @NotNull
    private Double score;

    @Builder.Default
    private List<IssueParamDTO> params = new ArrayList<>();

}
