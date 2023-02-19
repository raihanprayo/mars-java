package dev.scaraz.mars.common.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;

import java.io.*;

public class TempFileResource extends FileSystemResource {

    public TempFileResource(File file) {
        super(file);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new TempFileInputStream(getFile());
    }

    @Slf4j
    public static class TempFileInputStream extends FileInputStream {

        private final File file;

        public TempFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();

            if (file != null) {
                log.info("DELETING FILE {}", file.getAbsolutePath());
                FileUtils.deleteQuietly(file);
            }
        }
    }

}
