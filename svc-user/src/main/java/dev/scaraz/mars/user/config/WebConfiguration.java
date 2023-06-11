package dev.scaraz.mars.user.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.converter.*;
import dev.scaraz.mars.user.config.interceptor.LogInterceptor;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;
import static dev.scaraz.mars.common.utils.AppConstants.MimeType.MAPPED_MIME_TYPE;

@Slf4j
@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final MarsProperties marsProperties;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());

        Jackson2ObjectMapperBuilder jacksonBuilder = new Jackson2ObjectMapperBuilder()
                .modulesToInstall(new JavaTimeModule(), new ProblemModule(), new ConstraintViolationProblemModule())
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .serializerByType(Instant.class, new InstantSerializer())
                .serializerByType(Duration.class, new DurationSerializer())
                .deserializerByType(Instant.class, new InstantDeserializer())
                .deserializerByType(Duration.class, new DurationDeserializer());

        converters.add(new MappingJackson2HttpMessageConverter(jacksonBuilder.build()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
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

        source.setBasenames("classpath:i18n/messages");
        return source;
    }

}
