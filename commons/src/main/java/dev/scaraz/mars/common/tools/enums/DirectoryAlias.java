package dev.scaraz.mars.common.tools.enums;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

public enum DirectoryAlias {
    TMP,
    SHARED;

    @Getter
    private Path path;

    public File getDir() {
        return path.toFile();
    }

    @Component
    private static class DirectoryAliasInjector {
        private DirectoryAliasInjector(MarsProperties properties) {
            TMP.path = Path.of(properties.getDirectory().get(TMP));
            SHARED.path = Path.of(properties.getDirectory().get(SHARED));
        }
    }
}
