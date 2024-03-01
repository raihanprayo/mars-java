package dev.scaraz.mars.common;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;

@RequiredArgsConstructor

@Configuration
@ComponentScan({
        "dev.scaraz.mars.common.config",
        "dev.scaraz.mars.common.tools"
})
@EnableConfigurationProperties({MarsProperties.class})
public class MarsConfiguration {

    private final MarsProperties marsProperties;

    @PostConstruct
    private void init() {
        Path sharedPath = Path.of(marsProperties.getDirectory().get(DirectoryAlias.SHARED));
        File sharedDir = sharedPath.toFile();
        if (!sharedDir.exists()) sharedDir.mkdirs();

        Path tmpPath = Path.of(marsProperties.getDirectory().get(DirectoryAlias.TMP));
        File tmpDir = tmpPath.toFile();
        if (!tmpDir.exists()) tmpDir.mkdirs();
    }

}
