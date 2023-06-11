package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.csv.StoCsv;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.mapper.StoMapper;
import dev.scaraz.mars.user.repository.db.StoRepo;
import dev.scaraz.mars.user.service.StoService;
import dev.scaraz.mars.user.service.csv.StoCsvReader;
import io.github.avew.CsvewResultReader;
import io.github.avew.CsvewValidationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoServiceImpl implements StoService {

    private final StoRepo repo;
    private final StoMapper mapper;
    private final StoCsvReader csvReader;

    @Override
    public Sto save(Sto sto) {
        return repo.save(sto);
    }

    @Override
    public List<Sto> save(Iterable<Sto> sto) {
        return repo.saveAll(sto);
    }

    @Override
    @Transactional
    public List<Sto> importCsv(InputStream is) {
        CsvewResultReader<StoCsv> result = csvReader.process(is);
        if (result.isError()) {
            List<Violation> violations = new ArrayList<>();
            for (CsvewValidationDTO validation : result.getValidations()) {
                violations.add(new Violation(
                        String.format("Baris %s", validation.getLine()),
                        validation.getMessage()
                ));
            }

            throw new ConstraintViolationProblem(Status.BAD_REQUEST, violations);
        }
        else {
            return save(result.getValues().stream()
                    .map(mapper::toEntity)
                    .collect(Collectors.toList()));
        }
    }

}
