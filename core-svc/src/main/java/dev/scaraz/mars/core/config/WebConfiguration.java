package dev.scaraz.mars.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.converter.EnumsConverter;
import dev.scaraz.mars.core.config.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
    public static final Locale
            LOCALE_ID = new Locale("id"),
            LOCALE_EN = new Locale("en");

    private final MarsProperties marsProperties;

    private final ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EnumsConverter.StringToProduct());
        registry.addConverter(new EnumsConverter.StringToAgStatus());
        registry.addConverter(new EnumsConverter.StringToTcSource());
        registry.addConverter(new EnumsConverter.StringToTcStatus());
        registry.addConverter(new EnumsConverter.StringToWitel());
    }

    @PostConstruct
    private void init() {
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .setLocale(LOCALE_ID)
                .registerModules(
                        new JavaTimeModule(),
                        new ProblemModule().withStackTraces(false),
                        new ConstraintViolationProblemModule()
                );

        this.setSupportedLocales(List.of(LOCALE_EN, LOCALE_ID));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .combine(marsProperties.getCors());
    }

//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper()
//                .setLocale(LOCALE_ID)
//                .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
//                .registerModule(new ProblemModule().withStackTraces(false))
//                .registerModule(new ConstraintViolationProblemModule())
//                .registerModule(new JavaTimeModule())
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .configure(SerializationFeature.EAGER_SERIALIZER_FETCH, true);
//    }

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
