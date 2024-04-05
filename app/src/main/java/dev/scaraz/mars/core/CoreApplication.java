package dev.scaraz.mars.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.common.utils.CacheConstant;
import dev.scaraz.mars.core.config.event.app.AccountAccessEvent;
import dev.scaraz.mars.core.service.Initializer;
import dev.scaraz.mars.telegram.EnableTelegramSpring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@EnableScheduling
@EnableTelegramSpring
@SpringBootApplication
public class CoreApplication implements CommandLineRunner {

    private final CacheManager cacheManager;
    private final Initializer initializer;

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(CoreApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        log.info("\n--------------------------------------------------------------\n" +
                        "   Application\t\t: {}\n" +
                        "   Witel\t\t\t: {}\n" +
                        "   Build\t\t\t: {}\n" +
                        "   Profiles\t\t\t: {}\n" +
                        "   Host Local\t\t: http://localhost:{}\n" +
                        "   Host External\t: http://{}:{}\n" +
                        "--------------------------------------------------------------",
                environment.getProperty("spring.application.name"),
                environment.getProperty("mars.witel").toUpperCase(),
                environment.getProperty("mars.timestamp"),
                environment.getActiveProfiles(),
                port,
                InetAddress.getLocalHost().getHostAddress(),
                port
        );
    }

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        Optional.ofNullable(cacheManager.getCache(CacheConstant.ISSUES_KEYBOARD)).ifPresent(Cache::clear);

        AccountAccessEvent.setObectMapper(objectMapper);
        AccountAccessEvent.setApplicationEventPublisher(applicationEventPublisher);

        String imports = System.getenv("MARS_IMPORT");
        if (StringUtils.isNoneBlank(imports)) {
            log.info("Import initialization");
            initializer.importConfig();
            initializer.importRolesAndAdminAccount();
            initializer.importSto();
        }
    }

}
