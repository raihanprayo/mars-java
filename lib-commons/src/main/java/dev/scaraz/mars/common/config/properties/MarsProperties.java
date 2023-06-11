package dev.scaraz.mars.common.config.properties;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "mars")
public class MarsProperties {

    private Witel witel;

    private String secret;

    @Setter(AccessLevel.NONE)
    private CorsConfiguration cors = new CorsConfiguration();

    @Setter(AccessLevel.NONE)
    private CookieProperties cookie = new CookieProperties();

    @Setter(AccessLevel.NONE)
    private TelegramProperties telegram = new TelegramProperties();

    @Setter(AccessLevel.NONE)
    private I18nProperties i18n = new I18nProperties();

    @Setter(AccessLevel.NONE)
    private Map<DirectoryAlias, String> directory = new EnumMap<>(DirectoryAlias.class);

    @Getter
    @Setter
    @ToString
    public static class TelegramProperties {
        private String url = "https://api.telegram.org";
        private String token;
    }

    @Getter
    @Setter
    @ToString
    public static class CookieProperties {
        private String name = "MARSID";
        private String domain = "roc.scaraz.dev";
        private boolean httpOnly = true;
        private boolean secure = false;
        private String path = "/";
    }

    @Getter
    @Setter
    @ToString
    public static class I18nProperties {
        private boolean withDefaults = true;
        private List<String> externals = new ArrayList<>();
    }

}
