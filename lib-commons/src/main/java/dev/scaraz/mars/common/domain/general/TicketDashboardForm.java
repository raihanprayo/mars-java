package dev.scaraz.mars.common.domain.general;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDashboardForm implements Serializable {

    private Witel witel;

    @NotNull
    private String sto;

    /**
     * Issue ID
     */
    @NotNull
    private Long issue;

    @NotNull
    private String incidentNo;

    @NotNull
    private String serviceNo;

    private String note;

    private MultipartFile[] files;

    public Collection<MultipartFile> getFilesCollection() {
        if (files != null) return List.of(files);
        return List.of();
    }
}
