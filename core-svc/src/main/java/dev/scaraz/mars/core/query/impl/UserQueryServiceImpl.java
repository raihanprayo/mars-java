package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.repository.credential.UserRepo;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepo userRepo;

    @Override
    public DelegateUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByNameOrNik(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("cannot find user with NIK/Name " + username));
        return new DelegateUser(user);
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
