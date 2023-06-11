package dev.scaraz.mars.user.initializer;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.user.domain.Sto;
import dev.scaraz.mars.user.repository.db.StoRepo;
import dev.scaraz.mars.user.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor

@Component
public class FillStoScript {

    private static final String SCRIPT = "initial-sto";

    private final StoRepo stoRepo;
    private final ScriptService scriptService;

    @Autowired
    public void exec() {
        if (scriptService.isExecuted("initial-sto")) return;

        try (InputStream is = getClass().getResourceAsStream("/list-sto.csv")) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (StringUtils.isBlank(line)) continue;
                String[] splited = line.split(";");

                String witel = splited[0],
                        datel = splited[1],
                        alias = splited[2],
                        name = splited[3];

                Witel w;
                try {
                    w = Witel.valueOf(witel.toUpperCase());
                }
                catch (IllegalArgumentException ex) {
                    continue;
                }

                if (stoRepo.existsByWitelAndAlias(w, alias)) continue;
                stoRepo.save(Sto.builder()
                        .witel(w)
                        .datel(datel)
                        .alias(alias)
                        .name(name)
                        .build());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        scriptService.updateAsExecuted("initial-sto");
    }

}

