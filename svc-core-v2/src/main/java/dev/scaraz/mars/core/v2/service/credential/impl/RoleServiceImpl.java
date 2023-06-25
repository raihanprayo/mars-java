package dev.scaraz.mars.core.v2.service.credential.impl;

import dev.scaraz.mars.core.v2.domain.credential.Role;
import dev.scaraz.mars.core.v2.repository.db.credential.RoleRepo;
import dev.scaraz.mars.core.v2.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo repo;

    @Override
    public Role save(Role role) {
        return repo.save(role);
    }

    @Override
    public Role getOrCreate(String name) {
        return repo.findByAuthorityIgnoreCase(name)
                .orElseGet(() -> {
                    log.info("CREATE NEW ROLE {}", name);
                    return save(Role.builder()
                            .authority(name)
                            .build());
                });
    }

}
