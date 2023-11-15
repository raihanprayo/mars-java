package dev.scaraz.mars.app.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableEurekaServer
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);

        ConfigurableApplicationContext ctx = app.run(args);
        ConfigurableEnvironment env = ctx.getEnvironment();
        String port = env.getProperty("server.port");
        log.info("\n--------------------------------------------------------------\n" +
                        "   Application\t\t: {}\n" +
                        "   Profiles\t\t\t: {}\n" +
                        "   Host Local\t\t: http://localhost:{}\n" +
                        "   Host External\t: http://{}:{}\n" +
                        "--------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getActiveProfiles(),
                port,
                InetAddress.getLocalHost().getHostAddress(),
                port
        );
    }

}
