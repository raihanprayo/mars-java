package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.repository.db.order.StoRepo;
import dev.scaraz.mars.core.service.order.StoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class StoServiceImpl implements StoService {

    private final StoRepo repo;

    @Override
    public Sto save(Sto sto) {
        return repo.save(sto);
    }

    @Override
    @Transactional
    public Sto create(Sto sto) {
        log.info("CREATE NEW STO -- {}", sto);
        if (repo.existsByWitelAndAlias(sto.witel, sto.getAlias()))
            throw BadRequestException.duplicateEntity(Sto.class, "witel/alias", String.join("/", sto.getWitel().name(), sto.getAlias()));
        return save(sto);
    }

    @Override
    @Transactional
    public List<Sto> createFromFile(MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        String contentBytes = new String(is.readAllBytes());
        String[] lines = contentBytes.split("\n");

        List<Sto> result = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isBlank(line)) continue;

            String[] cols = line.split(";");
            String kode = cols[0];
            String nama = cols[1];
            String witel = cols[2];
            String datel = cols[3];

            try {
                Sto sto = create(Sto.builder()
                        .alias(kode)
                        .name(nama)
                        .witel(Witel.valueOf(witel.toUpperCase()))
                        .datel(datel)
                        .build());
                result.add(sto);
            }
            catch (IllegalArgumentException | BadRequestException ex) {
            }
        }

        return result;
    }

}
