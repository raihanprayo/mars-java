package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.domain.db.Sto;
import dev.scaraz.mars.app.administration.repository.db.StoRepo;
import dev.scaraz.mars.app.administration.service.app.StoService;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoServiceImpl implements StoService {

    private final StoRepo repo;

    @PostConstruct
    private void init() {
        try (InputStream is = getClass().getResourceAsStream("/data/list-sto.csv")) {
            List<Sto> stos = fromCsv(is);

            for (Sto sto : stos) {
                if (repo.existsByWitelAndAlias(sto.getWitel(), sto.getAlias())) continue;
                save(sto);
            }
        }
        catch (Exception e) {
        }
    }

    private List<Sto> fromCsv(InputStream is) throws IOException {
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");

        List<Sto> stos = new ArrayList<>();
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

            stos.add(Sto.builder()
                    .witel(w)
                    .datel(datel)
                    .alias(alias)
                    .name(name)
                    .build());
        }

        return stos;
    }

    @Override
    public Sto save(Sto sto) {
        return repo.save(sto);
    }

}
