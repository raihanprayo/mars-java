package dev.scaraz.mars.app.administration.web.dto;

import dev.scaraz.mars.app.administration.domain.db.Config;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigMapDTO {

    @Builder.Default
    private Map<String, Config> defaults = new LinkedHashMap<>();

    @Builder.Default
    private Map<String, Config> witels = new LinkedHashMap<>();

}
