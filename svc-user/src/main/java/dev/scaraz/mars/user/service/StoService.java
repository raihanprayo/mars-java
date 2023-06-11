package dev.scaraz.mars.user.service;

import dev.scaraz.mars.user.domain.db.Sto;

import java.io.InputStream;
import java.util.List;

public interface StoService {
    Sto save(Sto sto);

    List<Sto> save(Iterable<Sto> sto);

    List<Sto> importCsv(InputStream is);
}
