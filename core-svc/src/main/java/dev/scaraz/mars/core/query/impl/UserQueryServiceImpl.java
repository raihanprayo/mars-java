package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.UserSpecBuilder;
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
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepo repo;
    private final UserSpecBuilder specBuilder;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        // bisa nik, telegramId, email, & username
        Optional<User> user = repo.findByNik(username);

        // Cari dengan telegram id
        if (user.isEmpty()) {
            try {
                long telegramId = Long.parseLong(username);
                user = repo.findByTgId(telegramId);
            }
            catch (NumberFormatException ex) {
            }
        }

        // Cari dengan tg username
        if (user.isEmpty()) {
            user = repo.findByEmailOrTgUsername(username, username);
        }

        // Jika masih ga ada
        if (user.isEmpty())
            throw new UsernameNotFoundException("No user found");

        return user.get();
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<User> findAll(UserCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<User> findAll(UserCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(UserCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public Optional<User> findOne(UserCriteria criteria) {
        return repo.findOne(specBuilder.createSpec(criteria));
    }

    @Override
    public User findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(
                        User.class, "id", id));
    }

    @Override
    public User findByTelegramId(long tgId) {
        return repo.findByTgId(tgId)
                .orElseThrow(() -> NotFoundException.entity(
                        User.class, "telegramId", tgId));
    }

    @Override
    public User findByNik(String nik) {
        return repo.findByNik(nik)
                .orElseThrow(() -> NotFoundException.entity(User.class, "nik", nik));
    }

    @Override
    public boolean existByNik(String nik) {
        return repo.findByNik(nik)
                .isPresent();
    }

    @Override
    public boolean existByCriteria(UserCriteria criteria) {
        return repo.exists(specBuilder.createSpec(criteria));
    }

}
