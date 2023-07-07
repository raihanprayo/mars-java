package dev.scaraz.mars.admin;

import dev.scaraz.mars.admin.config.properties.KeycloakServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableAsync
//@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(KeycloakServerProperties.class)
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
