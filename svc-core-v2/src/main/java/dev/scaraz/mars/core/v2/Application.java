package dev.scaraz.mars.core.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("common");

        ConfigurableApplicationContext ctx = app.run(args);
        ConfigurableEnvironment env = ctx.getEnvironment();
        String port = env.getProperty("server.port");
        log.info("\n--------------------------------------------------------------\n" +
                        "   Application\t\t: {}\n" +
                        "   Witel\t\t\t: {}\n" +
                        "   Profiles\t\t\t: {}\n" +
                        "   Host Local\t\t: http://localhost:{}\n" +
                        "   Host External\t: http://{}:{}\n" +
                        "--------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("mars.witel").toUpperCase(),
                env.getActiveProfiles(),
                port,
                InetAddress.getLocalHost().getHostAddress(),
                port
        );
    }

}
