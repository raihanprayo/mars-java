package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusFormDTO {

    private TcStatus status;

    private String note;

    private Long solution;

    private MultipartFile[] files;

    private List<PhotoSize> photos;

    public Collection<MultipartFile> getFilesCollection() {
        if (files != null) return List.of(files);
        return List.of();
    }
}
