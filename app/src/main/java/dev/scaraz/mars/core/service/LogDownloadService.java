package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.tools.enums.DlStatus;
import dev.scaraz.mars.core.domain.LogDownload;
import dev.scaraz.mars.core.repository.db.LogDownloadRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogDownloadService {

    private final LogDownloadRepo repo;

    @Transactional
    public LogDownload create(String filename) {
        return repo.save(LogDownload.builder()
                .status(DlStatus.PROCESS)
                .filename(filename)
                .build());
    }

    @Transactional
    public void updatePath(long id, String path) {
        repo.updatePathById(id, DlStatus.COMPLETE, path);
    }

    @Transactional
    public void updateMessage(long id, String message) {
        repo.updatePathById(id, DlStatus.FAILED, message);
    }

}
