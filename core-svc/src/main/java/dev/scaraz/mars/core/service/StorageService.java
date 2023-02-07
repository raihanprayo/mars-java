package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.order.TicketAsset;
import dev.scaraz.mars.core.repository.order.TicketAssetRepo;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor

@Component
public class StorageService {
    public static final String TICKET_STORAGE_PATH = "/tickets";
    public static final List<String> IMAGE_MIME_TYPE = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );

    private final MarsProperties marsProperties;

    private final TelegramBotService botService;

    private final TicketAssetRepo tcAssetRepo;

    public TicketAsset addPhotoForTicket(Collection<PhotoSize> photos, Ticket ticket) {
        String no = ticket.getNo();
        Path storage = Path.of(marsProperties.getDirectory().get(DirectoryAlias.SHARED))
                .resolve("tickets")
                .resolve(no);

        if (!Files.exists(storage)) {
            storage.toFile().mkdirs();
        }

        String outputTicketNo = TICKET_STORAGE_PATH + "/" + no;
        TicketAsset asset = ticket.getAssets() != null ?
                ticket.getAssets() :
                TicketAsset.builder().ticket(ticket).build();

        PhotoSize photo = List.copyOf(photos).get(photos.size() - 1);
        try {
            String filename = photo.getFileUniqueId() + ".png";
            GetFile getFile = GetFile.builder()
                    .fileId(photo.getFileId())
                    .build();

            Path outputPath = storage.resolve(filename);
            asset.addPath(outputTicketNo + "/" + filename);

            botService.getClient().downloadFile(
                    botService.getClient().execute(getFile).getFilePath(),
                    outputPath.toFile()
            );
            log.debug("DOWNLOADED PHOTO TO {}", outputPath);
        }
        catch (TelegramApiException e) {
            log.error("Error at Add Photo", e);
        }

        if (asset.size() > 0) {
            log.debug("ADD {} TICKET ASSET(S) -- NO={}", asset.size(), ticket.getNo());
            asset = tcAssetRepo.save(asset);
        }

        return asset;
    }

    public TicketAsset addPhotoForTicketDashboard(Collection<MultipartFile> photos, Ticket ticket) {
        String no = ticket.getNo();
        Path storage = Path.of(marsProperties.getDirectory().get(DirectoryAlias.SHARED))
                .resolve("tickets")
                .resolve(no);

        if (!Files.exists(storage)) {
            storage.toFile().mkdirs();
        }

        String outputTicketNo = TICKET_STORAGE_PATH + "/" + no;
        TicketAsset asset = ticket.getAssets() != null ?
                ticket.getAssets() :
                TicketAsset.builder().ticket(ticket).build();

        for (MultipartFile photo : photos) {
            if (photo.isEmpty()) continue;

            String filename = photo.getName();
            Path outputPath = storage.resolve(filename);

            try {
                InputStream is = photo.getInputStream();
                try (OutputStream os = new FileOutputStream(outputPath.toFile())) {
                    os.write(is.readAllBytes());
                    os.flush();

                    asset.addPath(outputTicketNo + "/" + filename);
                }
            }
            catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (asset.size() > 0) {
            log.debug("ADD {} TICKET ASSET(S) -- NO={}", asset.size(), ticket.getNo());
            asset = tcAssetRepo.save(asset);
        }

        return asset;
    }

    public TicketAsset addPhotoForAgent(Ticket ticket, TicketAgent agent, Collection<MultipartFile> photos) {
        String no = ticket.getNo();
        Path storage = Path.of(marsProperties.getDirectory().get(DirectoryAlias.SHARED))
                .resolve("tickets")
                .resolve(no);

        if (!Files.exists(storage)) {
            storage.toFile().mkdirs();
        }

        String outputTicketNo = TICKET_STORAGE_PATH + "/" + no;
        TicketAsset asset = TicketAsset.builder()
                .agent(agent)
                .build();

        AtomicInteger counter = new AtomicInteger();
        for (MultipartFile photo : photos) {
            int index = counter.getAndIncrement();

            String contentType = photo.getContentType();
            if (contentType == null) continue;
            else if (!IMAGE_MIME_TYPE.contains(contentType)) continue;

            String filename = agent.getId() + "_" + index + (contentType.equals(IMAGE_MIME_TYPE.get(0)) ?
                    ".jpeg" : contentType.equals(IMAGE_MIME_TYPE.get(1)) ?
                    ".png" : ".webp");

            try {
                Path outputPath = storage.resolve(filename);
                FileUtils.writeByteArrayToFile(outputPath.toFile(), photo.getBytes());
                asset.addPath(outputTicketNo + "/" + filename);
            }
            catch (IOException e) {
            }
        }

        if (asset.size() > 0) {
            log.debug("ADD {} AGENT ASSET(S) -- TICKET NO={} AGENT={}", asset.size(),
                    ticket.getNo(),
                    agent.getId());
            asset = tcAssetRepo.save(asset);
        }

        return asset;
    }

    @Async
    public void addPhotoForTicketAsync(Collection<PhotoSize> photos, Ticket ticket) {
        this.addPhotoForTicket(photos, ticket);
    }

    @Async
    public void addPhotoForTicketDashboardAsync(Collection<MultipartFile> photos, Ticket ticket) {
        this.addPhotoForTicketDashboard(photos, ticket);
    }

    @Async
    public void addPhotoForAgentAsync(Ticket ticket, TicketAgent agent, Collection<MultipartFile> photos) {
        this.addPhotoForAgent(ticket, agent, photos);
    }

    public File createFileInTemporary(String filePath) {
        Path tmpFile = DirectoryAlias.TMP.getPath()
                .resolve(filePath);

        if (Files.exists(tmpFile)) {
            log.info("DELETING FILE IN TMP DIR: {}", tmpFile);
            FileUtils.deleteQuietly(tmpFile.toFile());
        }

        File result = tmpFile.toFile();
        try {
            if (!result.getParentFile().exists()) {
                log.debug("CREATE FILE DIRECTORY {}", result.getParentFile().getAbsolutePath());
                result.getParentFile().mkdirs();
            }

            boolean newFile = result.createNewFile();
            log.debug("CREATE NEW FILE -- {} | {}", newFile ? "OK" : "FAIL", result.getAbsolutePath());
        }
        catch (IOException e) {
            log.error("FAIL TO CREATE NEW FILE IN TMP", e);
        }

        return result;
    }

}
