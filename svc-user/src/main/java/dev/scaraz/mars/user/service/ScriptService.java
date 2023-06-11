package dev.scaraz.mars.user.service;

import dev.scaraz.mars.user.domain.ScriptInit;
import dev.scaraz.mars.user.repository.db.ScriptInitRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScriptService {

    private final ScriptInitRepo repo;

    public boolean isExecuted(String scriptName) {
        return repo.findById(scriptName)
                .map(ScriptInit::isExecuted)
                .orElseGet(() -> repo.save(ScriptInit.builder()
                                .id(scriptName)
                                .executed(false)
                                .build())
                        .isExecuted());
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAsExecuted(String scriptName) {
        repo.updateAsExecuted(scriptName);
    }

}
