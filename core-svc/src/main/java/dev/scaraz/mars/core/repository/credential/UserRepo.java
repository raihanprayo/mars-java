package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByNameOrNik(String name, String nik);

    Optional<User> findByTelegramId(long telegramId);

}
