package dev.scaraz.mars.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.scaraz.mars.user.config.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;

@Slf4j
@RequiredArgsConstructor

@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer, LocaleResolver {

    private final ObjectMapper jacksonObjectMapper;

    private final AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        jacksonObjectMapper.registerModule(new ProblemModule().withStackTraces(false));
        jacksonObjectMapper.registerModule(new JavaTimeModule());
        jacksonObjectMapper.registerModule(new ConstraintViolationProblemModule());
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(jacksonObjectMapper);
        jackson2HttpMessageConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_PROBLEM_JSON,
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.TEXT_EVENT_STREAM));

        converters.add(jackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);

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


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return headerLocaleResolver.resolveLocale(request);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        headerLocaleResolver.setLocale(request, response, locale);
    }
}
