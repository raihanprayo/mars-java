package dev.scaraz.mars.user.service.app.impl;

import dev.scaraz.mars.common.domain.general.UserDTO;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.user.datasource.domain.User;
import dev.scaraz.mars.user.datasource.repo.UserRepo;
import dev.scaraz.mars.user.mapper.CredentialMapper;
import dev.scaraz.mars.user.service.app.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo repo;
    private final CredentialMapper mapper;
    @Override
    public void check(UserDetails toCheck) {
        if (!toCheck.isEnabled())
            throw new UnauthorizedException("Account disabled");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByNikOrEmail(username, username);
        if (user == null) {
            try {
                long tgId = Long.parseLong(username);
                user = repo.findByInfoTgId(tgId);
            }
            catch (Exception ex) {
            }
        }

        if (user == null)
            throw new UsernameNotFoundException("credential not found");

        return user;
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    @Override
    public UserDTO save(UserDTO dto) {
        User user = mapper.fromDTO(dto);
        return mapper.toDTO(repo.save(user));
    }

}
