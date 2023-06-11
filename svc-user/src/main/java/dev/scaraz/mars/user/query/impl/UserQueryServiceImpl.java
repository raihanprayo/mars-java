package dev.scaraz.mars.user.query.impl;

import dev.scaraz.mars.user.domain.MarsUser;
import dev.scaraz.mars.user.query.UserQueryService;
import dev.scaraz.mars.user.query.spec.UserSpecBuilder;
import dev.scaraz.mars.user.repository.db.MarsUserRepo;
import dev.scaraz.mars.user.web.criteria.UserCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final MarsUserRepo repo;
    private final UserSpecBuilder specBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByNik(username)
                .orElseThrow(() -> new UsernameNotFoundException("cannot find user with nik " + username));
    }

    @Override
    public List<MarsUser> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<MarsUser> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<MarsUser> findAll(UserCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<MarsUser> findAll(UserCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }
}
