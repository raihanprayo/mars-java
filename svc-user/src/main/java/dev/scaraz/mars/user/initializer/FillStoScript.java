package dev.scaraz.mars.user.initializer;

import dev.scaraz.mars.user.domain.csv.StoCsv;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.mapper.StoMapper;
import dev.scaraz.mars.user.repository.db.StoRepo;
import dev.scaraz.mars.user.service.ScriptService;
import dev.scaraz.mars.user.service.csv.StoCsvReader;
import io.github.avew.CsvewResultReader;
import io.github.avew.CsvewValidationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Component
public class FillStoScript {

    private static final String SCRIPT = "initial-sto";

    private final StoRepo stoRepo;
    private final StoMapper stoMapper;
    private final StoCsvReader stoCsvReader;
    private final ScriptService scriptService;

    @Autowired
    public void exec() {
        if (scriptService.isExecuted("initial-sto")) return;

        try (InputStream is = getClass().getResourceAsStream("/list-sto.csv")) {
            CsvewResultReader<StoCsv> result = stoCsvReader.process(is);
            if (result.isError()) {
                log.error("STO Validation Error:");
                for (CsvewValidationDTO validation : result.getValidations()) {
                    log.error("- {}", validation.getMessage());
                }
            }
            else {
                List<Sto> stos = result.getValues().stream()
                        .map(stoMapper::toEntity)
                        .collect(Collectors.toList());

                stoRepo.saveAll(stos);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        scriptService.updateAsExecuted("initial-sto");
    }

}

