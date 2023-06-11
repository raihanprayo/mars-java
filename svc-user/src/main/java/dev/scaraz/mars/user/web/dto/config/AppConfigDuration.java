package dev.scaraz.mars.user.web.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

@Getter
@Setter
@SuperBuilder
public class AppConfigDuration extends AppConfigDTO<Duration> {
}
