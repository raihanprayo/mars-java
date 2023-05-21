package dev.scaraz.mars.user.datasource.repo;

import dev.scaraz.mars.user.datasource.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends WithSpecRepository<User, String> {

    boolean existsByNik(String nik);

    User findByNikOrEmail(String username, String email);

    User findByInfoTgId(long tgId);

}
