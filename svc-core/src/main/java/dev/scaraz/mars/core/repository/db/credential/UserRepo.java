package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByNik(String nik);
    Optional<User> findByEmailOrTgUsername(String username, String email);

    Optional<User> findByTgId(long telegramId);

}
