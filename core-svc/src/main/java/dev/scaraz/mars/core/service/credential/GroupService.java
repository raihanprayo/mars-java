package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.Group;
import org.springframework.transaction.annotation.Transactional;

public interface GroupService {
    @Transactional
    Group create(String name, boolean canLogin);
}
