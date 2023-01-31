package dev.scaraz.mars.core;

import dev.scaraz.mars.core.service.InitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor

@EnableScheduling
@SpringBootApplication
public class CoreApplication implements CommandLineRunner {
    private final InitializerService initializer;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        initializer.checkWitel();
    }

    @Override
    public void run(String... args) {
        initializer.preInitIssue();

        initializer.preInitRolesAndCreateAdmin();
//        initializer.preInitGroups();
        initializer.preInitAppConfigs();
    }
}
