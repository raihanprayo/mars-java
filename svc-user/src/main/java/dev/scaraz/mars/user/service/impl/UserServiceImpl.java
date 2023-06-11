package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.MarsUser;
import dev.scaraz.mars.user.repository.db.MarsUserRepo;
import dev.scaraz.mars.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final MarsUserRepo repo;

    @Override
    public MarsUser save(MarsUser user) {
        return repo.save(user);
    }

}
