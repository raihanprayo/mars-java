package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.user.domain.AppConfig;
import dev.scaraz.mars.user.web.dto.config.*;
import org.springframework.stereotype.Component;

@Component
public class AppConfigMapper {

    private <T, C extends AppConfigDTO<T>, B extends AppConfigDTO.AppConfigDTOBuilder<T, C, B>> B
    baseMap(AppConfig config, AppConfigDTO.AppConfigDTOBuilder<T, C, B> builder) {
        return builder.type(config.getType())
                .name(config.getName())
                .title(config.getTitle())
                .description(config.getDescription());
    }

    public AppConfigDTO<?> toDTO(AppConfig config) {
        switch (config.getType()) {
            case STRING:
                return toStringDTO(config);
            case DURATION:
                return toDurationDTO(config);
            case NUMBER:
                return toNumberDTO(config);
            case BOOLEAN:
                return toBooleanDTO(config);
        }
        return null;
    }

    public AppConfigString toStringDTO(AppConfig config) {
        return baseMap(config, AppConfigString.builder())
                .value(config.getValue())
                .build();
    }

    public AppConfigDuration toDurationDTO(AppConfig config) {
        return baseMap(config, AppConfigDuration.builder())
                .value(config.getAsDuration())
                .build();
    }

    public AppConfigNumber toNumberDTO(AppConfig config) {
        return baseMap(config, AppConfigNumber.builder())
                .value(config.getAsNumber())
                .build();
    }

    public AppConfigBoolean toBooleanDTO(AppConfig config) {
        return baseMap(config, AppConfigBoolean.builder())
                .value(config.getAsBoolean())
                .build();
    }

}
