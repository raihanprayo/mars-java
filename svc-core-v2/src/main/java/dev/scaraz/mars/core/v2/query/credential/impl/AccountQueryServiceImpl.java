package dev.scaraz.mars.core.v2.query.credential.impl;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.query.credential.AccountQueryService;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepo repo;


    @Override
    public List<Account> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = repo.findByUsernameIgnoreCase(username);
        if (account.isEmpty())
            throw new UsernameNotFoundException("Unknown username");
        return account.get();
    }
}
