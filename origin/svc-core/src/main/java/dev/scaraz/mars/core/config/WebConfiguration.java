package dev.scaraz.mars.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.dynamic.DynamicJsonDeserializer;
import dev.scaraz.mars.common.domain.dynamic.DynamicJsonSerializer;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import dev.scaraz.mars.common.tools.converter.*;
import dev.scaraz.mars.core.config.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;
import static dev.scaraz.mars.common.tools.Translator.LANG_ID;
import static dev.scaraz.mars.common.utils.AppConstants.MimeType.MAPPED_MIME_TYPE;

@Slf4j
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration extends AcceptHeaderLocaleResolver implements WebMvcConfigurer, LocaleResolver {

    private final MarsProperties marsProperties;

    @PostConstruct
    private void init() {
        this.setDefaultLocale(LANG_ID);
        this.setSupportedLocales(List.of(LANG_EN, LANG_ID));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilder() {
        return (builder) -> {
            log.debug("Jackson ObjectMapper customizer");
            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                    .serializerByType(Duration.class, new DurationSerializer())
                    .serializerByType(Instant.class, new InstantSerializer())
                    .deserializerByType(Duration.class, new DurationDeserializer())
                    .deserializerByType(Instant.class, new InstantDeserializer())

                    .serializerByType(DynamicType.class, new DynamicJsonSerializer())
                    .deserializerByType(DynamicType.class, new DynamicJsonDeserializer());
//                    .modules(
//                            new ProblemModule().withStackTraces(false),
//                            new ConstraintViolationProblemModule());
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
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

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaTypes(MAPPED_MIME_TYPE);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        WebMvcConfigurer.super.configureMessageConverters(converters);
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

}
