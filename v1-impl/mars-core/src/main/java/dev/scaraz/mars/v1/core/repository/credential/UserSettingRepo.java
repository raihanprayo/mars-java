package dev.scaraz.mars.v1.core.repository.credential;

import dev.scaraz.mars.v1.core.domain.credential.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepo extends JpaRepository<UserSetting, String> {

    Optional<UserSetting> findByUserTgId(long telegramId);
}
