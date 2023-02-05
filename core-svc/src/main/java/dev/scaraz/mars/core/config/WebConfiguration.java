package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.converter.EnumsConverter;
import dev.scaraz.mars.common.tools.converter.StringToLocalDateConverter;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.interceptor.LogInterceptor;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;
import static dev.scaraz.mars.common.tools.Translator.LANG_ID;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer, LocaleResolver {

    private final MarsProperties marsProperties;

    private final AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();

    private final AuditProvider auditProvider;

    @PostConstruct
    private void init() {
        headerLocaleResolver.setDefaultLocale(LANG_ID);
        headerLocaleResolver.setSupportedLocales(List.of(LANG_EN, LANG_ID));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor(auditProvider));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .combine(marsProperties.getCors());
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setDefaultLocale(LANG_EN);
        source.setUseCodeAsDefaultMessage(true);
        source.setFallbackToSystemLocale(true);

        source.setBasenames(
                "classpath:i18n/messages",
                "classpath:i18n/telegram"
        );

        for (String external : marsProperties.getI18n().getExternals()) {
            if (StringUtils.isBlank(external)) continue;
            source.addBasenames(external);
        }

        return source;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        User user = SecurityUtil.getCurrentUser();

        Locale locale;
        if (user != null) locale = user.getSetting().getLang();
        else locale = headerLocaleResolver.resolveLocale(request);

        LocaleContextHolder.setLocale(locale, true);
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        headerLocaleResolver.setLocale(request, response, locale);
    }
}
