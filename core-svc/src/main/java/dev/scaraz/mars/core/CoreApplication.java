package dev.scaraz.mars.core;

import dev.scaraz.mars.core.service.InitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@RequiredArgsConstructor

@SpringBootApplication
@EnableScheduling
public class CoreApplication implements CommandLineRunner {
    private final InitializerService initializer;

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

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        initializer.checkWitel();
    }

    @Override
    public void run(String... args) {
        initializer.initIssue();

        initializer.initRolesAndCreateAdmin();
//        initializer.preInitGroups();
        initializer.initAppConfigs();
        initializer.initSto();
    }
}
