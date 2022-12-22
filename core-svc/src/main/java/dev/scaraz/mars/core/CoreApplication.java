package dev.scaraz.mars.core;

import dev.scaraz.mars.core.service.InitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@RequiredArgsConstructor

@SpringBootApplication
public class CoreApplication implements CommandLineRunner {

    private final InitializerService initializer;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... args) {
        initializer.preInitRoles();
        initializer.preInitGroups();
        initializer.preInitIssue();
    }
}
