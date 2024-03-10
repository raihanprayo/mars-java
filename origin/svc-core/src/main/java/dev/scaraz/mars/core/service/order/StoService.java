package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Sto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StoService {
    Sto save(Sto sto);

    Sto create(Sto sto);

    List<Sto> createFromFile(MultipartFile file) throws IOException;
}
