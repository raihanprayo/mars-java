package dev.scaraz.mars.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.core.config.event.app.AccountAccessEvent;
import dev.scaraz.mars.core.domain.event.RefreshIssueInlineButtons;
import dev.scaraz.mars.core.service.Initializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@RequiredArgsConstructor

@SpringBootApplication
@EnableScheduling
public class CoreApplication implements CommandLineRunner {
    private final Initializer initializer;

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(CoreApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        log.info("\n--------------------------------------------------------------\n" +
                        "   Application\t\t: {}\n" +
                        "   Witel\t\t\t: {}\n" +
                        "   Profiles\t\t\t: {}\n" +
                        "   Host Local\t\t: http://localhost:{}\n" +
                        "   Host External\t: http://{}:{}\n" +
                        "--------------------------------------------------------------",
                environment.getProperty("spring.application.name"),
                environment.getProperty("mars.witel").toUpperCase(),
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
        AccountAccessEvent.setObectMapper(objectMapper);
        AccountAccessEvent.setApplicationEventPublisher(applicationEventPublisher);

        String imports = System.getenv("MARS_IMPORT");
        if (StringUtils.isNoneBlank(imports)) {
            log.info("Import initialization");
            initializer.importConfig();
            initializer.importIssue();
            initializer.importRolesAndAdminAccount();
            initializer.importSto();
        }

        applicationEventPublisher.publishEvent(new RefreshIssueInlineButtons());
    }
}
