package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.common.tools.enums.DlStatus;
import dev.scaraz.mars.core.domain.LogDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogDownloadRepo extends JpaRepository<LogDownload, Long>, JpaSpecificationExecutor<LogDownload> {

    @Modifying
    @Query("update LogDownload ld set " +
            "ld.path = :path, " +
            "ld.status = :status " +
            "where ld.id = :id")
    void updatePathById(long id, DlStatus status, String path);


    @Modifying
    @Query("update LogDownload ld set " +
            "ld.message = :message, " +
            "ld.status = :status " +
            "where ld.id = :id")
    void updateMessageById(long id, DlStatus status, String message);

}
