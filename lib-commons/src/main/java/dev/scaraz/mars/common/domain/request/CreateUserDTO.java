package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @NotNull
    private String name;

    @NotNull
    private String nik;

    @NotNull
    private String phone;

    private String email;

    private String username;

    @NotNull
    private Boolean active;

    private String group;

    private Witel witel;

    private String sto;

    /**
     * isi role idnya aja
     */
    @Min.List(@Min(1))
    @Builder.Default
    private List<String> roles = new ArrayList<>();

}
