package dev.scaraz.mars.v1.core.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.v1.core.domain.order.AgentWorklog;
import dev.scaraz.mars.v1.core.domain.order.Ticket;
import dev.scaraz.mars.v1.core.util.Util;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@Service
public class StorageService {
    public static final String TICKET_STORAGE_PATH = "/tickets";
    public static final String WORKSPACE_STORAGE_PATH = "/workspace";
    public static final String WORKLOG_STORAGE_PATH = "/worklog";

    public static final List<String> IMAGE_MIME_TYPE = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );

    private final MarsProperties marsProperties;

    private final TelegramBotService botService;


    private Path getTmpDirectory() {
        return Path.of(marsProperties.getDirectory().get(DirectoryAlias.TMP));
    }

    private Path getSharedDirectory() {
        return Path.of(marsProperties.getDirectory().get(DirectoryAlias.SHARED));
    }

    public File createFile(DirectoryAlias target, String... filepaths) {
        log.debug("CREATE NEW FILE TO {}", target);
        Path path = target.getPath();

        String joined = Arrays.stream(filepaths)
                .filter(this::warnNullSegment)
                .collect(Collectors.joining("/"));

        Path outPath = path.resolve(joined);
        Util.createDirIfNotExist(outPath.getParent());

        File outFile = outPath.toFile();
        if (outFile.exists()) {
            log.debug("DELETING FILE IN {} DIR: {}", target, outFile);
            FileUtils.deleteQuietly(outFile);
        }

        try {
            boolean newFile = outFile.createNewFile();
            log.debug("CREATE NEW FILE -- {} | {}", newFile ? "OK" : "FAIL", outFile);
        }
        catch (IOException e) {
            log.warn("FAIL TO CREATE NEW FILE ({})", outFile, e);
        }
        return outFile;
    }

    public void addSharedAsset(InputStream is, String filename, String... dirPaths) {
        try (is) {
            String dirPath = Stream.of(dirPaths)
                    .filter(this::warnNullSegment)
                    .map(t -> t.startsWith("/") ? t.substring(1) : t)
                    .collect(Collectors.joining("/"));

            log.info("ADDING SHARED ASSET TO -- {}/{}", dirPath, filename);

            Path dir;
            if (StringUtils.isBlank(dirPath)) dir = getSharedDirectory();
            else dir = getSharedDirectory().resolve(dirPath);

            Util.createDirIfNotExist(dir);

            Path output = dir.resolve(filename);
            try (OutputStream os = new FileOutputStream(output.toFile())) {
                os.write(is.readAllBytes());
                os.flush();
            }
        }
        catch (IOException ex) {
            log.error("Fail to add shared asset", ex);
        }
    }


    @Async
    public void addTelegramAssets(Ticket ticket, Collection<PhotoSize> assets, String... dirPaths) {
        addTelegramAsset(assets, concatSegments(dirPaths,
                TICKET_STORAGE_PATH,
                ticket.getNo()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void addTelegramAssets(Ticket ticket, AgentWorklog worklog, Collection<PhotoSize> assets, String... dirPaths) {
        addTelegramAssets(ticket, assets, concatSegments(dirPaths,
                WORKSPACE_STORAGE_PATH,
                worklog.getWorkspace().getId(),
                WORKLOG_STORAGE_PATH,
                worklog.getId()));
    }

    @Async
    public void addDashboardAssets(Ticket ticket, Collection<MultipartFile> assets, String... dirPaths) {
        addDashboardAsset(assets, concatSegments(dirPaths,
                TICKET_STORAGE_PATH,
                ticket.getNo()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void addDashboardAssets(Ticket ticket, AgentWorklog worklog, Collection<MultipartFile> assets, String... dirPaths) {
        addDashboardAssets(ticket, assets, concatSegments(dirPaths,
                WORKSPACE_STORAGE_PATH,
                worklog.getWorkspace().getId(),
                WORKLOG_STORAGE_PATH,
                worklog.getId()));
    }


    private void addTelegramAsset(Collection<PhotoSize> assets, String... dirPaths) {
        if (assets == null || assets.isEmpty()) return;

        log.info("ADDING TELEGRAM SHARED ASSET");

        PhotoSize lastItem = Util.getLastItem(assets);
        if (lastItem != null) {
            String filename = lastItem.getFileUniqueId() + ".png";
            try {
                InputStream is = botService.getClient().downloadFileAsStream(
                        botService.getClient()
                                .execute(GetFile.builder()
                                        .fileId(lastItem.getFileId())
                                        .build())
                                .getFilePath());
                addSharedAsset(is, filename, dirPaths);
            }
            catch (TelegramApiException ex) {
                log.error("Fail to add TELEGRAM asset", ex);
            }
        }
    }

    private void addDashboardAsset(Collection<MultipartFile> assets, String... dirPaths) {
        if (assets == null || assets.isEmpty()) return;

        log.info("ADDING DASHBOARD SHARED ASSET");

        for (MultipartFile asset : assets) {
            if (asset.isEmpty()) continue;

            try (InputStream is = asset.getInputStream()) {
                addSharedAsset(is, asset.getOriginalFilename(), dirPaths);
            }
            catch (IOException ex) {
                log.error("Fail to add DASHBOARD asset", ex);
            }
        }
    }

    private boolean warnNullSegment(String t) {
        boolean b = StringUtils.isNoneBlank(t);
        if (!b) log.warn("Please make sure there's no null/empty segment");
        return b;
    }

    private String[] concatSegments(String[] dirPaths, Object... shifts) {
        return Stream.concat(Stream.of(shifts).map(Object::toString), Stream.of(dirPaths))
                .toArray(String[]::new);
    }

}
