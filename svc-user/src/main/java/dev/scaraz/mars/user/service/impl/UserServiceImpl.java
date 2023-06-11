package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.domain.db.Roles;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.query.RoleQueryService;
import dev.scaraz.mars.user.query.StoQueryService;
import dev.scaraz.mars.user.query.UserQueryService;
import dev.scaraz.mars.user.repository.db.MarsUserRepo;
import dev.scaraz.mars.user.repository.db.RolesRepo;
import dev.scaraz.mars.user.service.UserService;
import dev.scaraz.mars.user.web.dto.CreateUserDTO;
import dev.scaraz.mars.user.web.dto.UpdateRoleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final MarsUserRepo repo;
    private final UserQueryService queryService;

    private final RolesRepo rolesRepo;
    private final RoleQueryService roleQueryService;
    private final StoQueryService stoQueryService;

    @Override
    public MarsUser save(MarsUser user) {
        return repo.save(user);
    }

    @Override
    @Transactional
    public MarsUser create(CreateUserDTO req) {
        log.info("CREATE NEW USER: {}", req);
        MarsUser user = save(MarsUser.builder()
                .nik(req.getNik())
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .witel(req.getWitel())
                .telegram(req.getTelegram())
                .build());

        if (StringUtils.isNoneBlank(req.getSto())) {
            Sto sto = stoQueryService.findByIdOrName(req.getSto());
            user.setSto(sto.getId());
        }


        List<Roles> roles = req.getRoles().stream()
                .map(roleQueryService::findByName)
                .map(r -> Roles.of(r, user))
                .collect(Collectors.toList());

        user.setRoles(rolesRepo.saveAll(roles).stream()
                .map(Roles::getRole)
                .collect(Collectors.toSet()));
        return user;
    }

    @Override
    @Transactional
    public MarsUser updateRole(String id, UpdateRoleDTO req) {
        log.info("UPDATE USER ({}) ROLE: {}", id, req);
        MarsUser user = queryService.findById(id);
        Set<Role> attached = user.getRoles();

        req.getRemove().stream()
                .map(roleQueryService::findByName)
                .forEach(attached::remove);

        req.getAdd().stream()
                .map(roleQueryService::findByName)
                .forEach(attached::add);

        rolesRepo.deleteAllByUserId(id);
        user.setRoles(attached.stream()
                .map(r -> rolesRepo.save(Roles.of(r, user)))
                .map(Roles::getRole)
                .collect(Collectors.toSet()));
        return user;
    }

}
