package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIssueDTO {

    @NotNull
    private String name;

    @NotNull
    private Product product;

    private String alias;

    @NotNull
    private String description;

    @Builder.Default
    private List<IssueParamDTO> params = new ArrayList<>();

}
