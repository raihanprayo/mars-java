package dev.scaraz.mars.common.config.properties;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.EnumMap;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "mars", ignoreUnknownFields = false)
public class MarsProperties {

    private String secret;

    private CorsConfiguration cors = new CorsConfiguration();
    private TelegramProperties telegram = new TelegramProperties();
    private EnumMap<DirectoryAlias, String> directory = new EnumMap<>(DirectoryAlias.class);

    @Getter
    @Setter
    @ToString
    public static class TelegramProperties {
        private String url = "https://api.telegram.org";
        private String token;
    }

}
