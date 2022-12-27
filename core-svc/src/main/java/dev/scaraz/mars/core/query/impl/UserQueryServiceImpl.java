package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.credential.UserRepo;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserQueryServiceImpl extends QueryBuilder implements UserQueryService {

    private final UserRepo userRepo;

    @Override
    public DelegateUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByNameOrNik(username, username);
        if (user.isEmpty()) user = userRepo.findByCredentialUsername(username);

        if (user.isEmpty())
            throw new UsernameNotFoundException("cannot find user with NIK/Name " + username);

        return new DelegateUser(user.get());
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    @Override
    public List<User> findAll(UserCriteria criteria) {
        return userRepo.findAll(createSpecification(criteria));
    }

    @Override
    public Page<User> findAll(UserCriteria criteria, Pageable pageable) {
        return userRepo.findAll(createSpecification(criteria), pageable);
    }

    @Override
    public User findById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(
                        User.class, "id", id));
    }

    @Override
    public User findByTelegramId(long tgId) {
        return userRepo.findByTelegramId(tgId)
                .orElseThrow(() -> NotFoundException.entity(
                        User.class, "telegramId", tgId));
    }

}
