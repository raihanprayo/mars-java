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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor

@Component
public class StorageService {
    public static final String TICKET_STORAGE_PATH = "/tickets";

    private final MarsProperties marsProperties;

    private final TelegramBotService botService;

    private final TicketAssetRepo tcAssetRepo;

    public TicketAsset addPhotoForTicket(Collection<PhotoSize> photos, Ticket ticket) {
        String no = ticket.getNo();
        Path storage = Path.of(marsProperties.getDirectory().get(DirectoryAlias.STORAGE))
                .resolve("tickets")
                .resolve(no);

        if (!Files.exists(storage)) {
            storage.toFile().mkdirs();
        }


        String outputTicketNo = TICKET_STORAGE_PATH + "/" + no;
        TicketAsset asset = new TicketAsset();

        PhotoSize photo = List.copyOf(photos).get(photos.size() - 1);
        try {
            String filename = photo.getFileUniqueId() + ".png";
            GetFile getFile = GetFile.builder()
                    .fileId(photo.getFileId())
                    .build();

            Path outputPath = storage.resolve(filename);
            asset.setTicket(ticket);
            asset.addPath(outputTicketNo + "/" + filename);

            log.debug("DOWNLOADING PHOTO TO {}", outputPath);
            botService.getClient().downloadFile(
                    botService.getClient().execute(getFile).getFilePath(),
                    outputPath.toFile()
            );
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        if (asset.size() > 0)
            asset = tcAssetRepo.save(asset);

        return asset;
    }

    public TicketAsset addPhotoForAgent(TicketAgent agent) {
        return null;
    }

    @Async
    public void addPhotoForTicketAsync(Collection<PhotoSize> photos, Ticket ticket) {
        this.addPhotoForTicket(photos, ticket);
    }

}
