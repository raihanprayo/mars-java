package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface GroupService {
    Group create(String name, boolean canLogin);

    Group addUser(Group group, User user, @Nullable Role groupRole);
}
