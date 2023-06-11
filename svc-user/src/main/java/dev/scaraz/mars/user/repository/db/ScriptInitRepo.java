package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.ScriptInit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptInitRepo extends JpaRepository<ScriptInit, String> {

    @Modifying
    @Query("update ScriptInit md set " +
            "md.executed = true, " +
            "md.errorMessage = null " +
            "where md.id = :id")
    void updateAsExecuted(String id);

    @Modifying
    @Query("update ScriptInit md set " +
            "md.executed = false, " +
            "md.errorMessage = :message " +
            "where md.id = :id")
    void updateScriptMessage(String id, String message);

}
