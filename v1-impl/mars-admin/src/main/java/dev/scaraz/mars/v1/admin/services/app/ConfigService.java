package dev.scaraz.mars.v1.admin.services.app;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import dev.scaraz.mars.v1.admin.domain.app.ConfigTag;
import dev.scaraz.mars.common.domain.ConfigDTO;

public interface ConfigService {
    Config save(Config tag);

    ConfigTag save(ConfigTag tag);

    Config get(String key);

    //    @Transactional
    Config update(ConfigDTO dto);
}
