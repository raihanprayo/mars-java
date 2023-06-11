package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.repository.db.RoleRepo;
import dev.scaraz.mars.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo repo;

    @Override
    public Role save(Role role) {
        return repo.save(role);
    }

    @Override
    public List<Role> create(List<String> roleNames) {
        return repo.saveAll(roleNames.stream()
                .map(Role::new)
                .collect(Collectors.toList()));
    }

}
