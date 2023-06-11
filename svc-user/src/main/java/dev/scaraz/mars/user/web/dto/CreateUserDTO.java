package dev.scaraz.mars.user.web.dto;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO implements Serializable {

    @NotNull
    private String nik;
    @NotNull
    private String name;
    @NotNull
    private String phone;
    private String email;


    @NotNull
    private Long telegram;

    @NotNull
    private Witel witel;

    private String sto;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

}
