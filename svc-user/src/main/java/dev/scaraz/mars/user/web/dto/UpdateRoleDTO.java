package dev.scaraz.mars.user.web.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {

    @Builder.Default
    private Set<String> remove = new HashSet<>();

    @Builder.Default
    private Set<String> add = new HashSet<>();

}
