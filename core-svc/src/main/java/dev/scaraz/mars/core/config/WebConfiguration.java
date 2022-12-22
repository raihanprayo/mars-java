package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
    private static final Locale
            LOCALE_ID = new Locale("id"),
            LOCALE_EN = new Locale("en");

    private final MarsProperties marsProperties;

    @PostConstruct
    private void init() {
        this.setSupportedLocales(List.of(LOCALE_EN, LOCALE_ID));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .combine(marsProperties.getCors());
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setDefaultLocale(Locale.getDefault());
        source.setUseCodeAsDefaultMessage(true);
        source.setFallbackToSystemLocale(true);
        return source;
    }

}
