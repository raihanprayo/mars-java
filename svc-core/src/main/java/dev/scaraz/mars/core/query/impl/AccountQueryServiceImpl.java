package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.AccountSpecBuilder;
import dev.scaraz.mars.core.repository.db.credential.AccountRepo;
import dev.scaraz.mars.security.MarsUserContext;
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
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepo repo;
    private final AccountSpecBuilder specBuilder;

    @Override
    public Account loadUserByUsername(String username) throws UsernameNotFoundException {
        // bisa nik, telegramId, email, & username
        Optional<Account> user = repo.findByNik(username);

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
    public List<Account> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Account> findAll(UserCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Account> findAll(UserCriteria criteria, Pageable pageable) {
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
    public Optional<Account> findOne(UserCriteria criteria) {
        return repo.findOne(specBuilder.createSpec(criteria));
    }

    @Override
    public Account findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(
                        Account.class, "id", id));
    }

    @Override
    public Account findByCurrentAccess() {
        return findById(MarsUserContext.getId());
    }

    @Override
    public Optional<Account> findByIdOpt(String id) {
        return repo.findById(id);
    }

    @Override
    public Account findByTelegramId(long tgId) {
        return repo.findByTgId(tgId)
                .orElseThrow(() -> NotFoundException.entity(
                        Account.class, "telegramId", tgId));
    }

    @Override
    public Account findByNik(String nik) {
        return repo.findByNik(nik)
                .orElseThrow(() -> NotFoundException.entity(Account.class, "nik", nik));
    }

    @Override
    public Account findByNikOrTelegramId(String nikOrTelegramId) {
        Optional<Account> account = repo.findByNik(nikOrTelegramId);
        if (account.isEmpty())
            account = repo.findByTgId(Long.parseLong(nikOrTelegramId));

        if (account.isEmpty())
            throw NotFoundException.entity(Account.class, "nik/telegram", nikOrTelegramId);
        return account.get();
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
