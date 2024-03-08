package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSolutionDTO {

    @NotNull
    private String name;

    private String description;

    private Product product;

}
