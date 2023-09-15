package dev.scaraz.mars.app.administration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.scaraz.mars.common.domain.dynamic.DynamicJsonDeserializer;
import dev.scaraz.mars.common.domain.dynamic.DynamicJsonSerializer;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import dev.scaraz.mars.common.tools.converter.DurationDeserializer;
import dev.scaraz.mars.common.tools.converter.DurationSerializer;
import dev.scaraz.mars.common.tools.converter.InstantDeserializer;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.jackson.ProblemModule;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

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
                    .deserializerByType(DynamicType.class, new DynamicJsonDeserializer())

                    .modules(new ProblemModule().withStackTraces(false));
        };
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

        return source;
    }

}
