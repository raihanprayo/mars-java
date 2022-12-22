package dev.scaraz.mars.common;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "dev.scaraz.mars.common.config",
        "dev.scaraz.mars.common.tools"
})
@EnableConfigurationProperties({
        MarsProperties.class
})
public class MarsConfiguration {
}
