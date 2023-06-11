package dev.scaraz.mars.user.web.dto.config;

import dev.scaraz.mars.user.domain.AppConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class AppConfigString extends AppConfigDTO<String> {
}
