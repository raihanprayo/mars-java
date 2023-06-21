package dev.scaraz.mars.core.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
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
}
