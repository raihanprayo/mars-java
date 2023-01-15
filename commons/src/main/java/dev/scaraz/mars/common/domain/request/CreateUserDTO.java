package dev.scaraz.mars.common.domain.request;

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
public class CreateUserDTO {

    @NotNull
    private String name;

    @NotNull
    private String nik;

    @NotNull
    private String phone;

    private String email;

    private String username;

    private Boolean active;

    private String group;

    /**
     * isi role idnya aja
     */
    @Builder.Default
    private List<String> roles = new ArrayList<>();

}
