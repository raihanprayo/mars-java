package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSettingRepo extends JpaRepository<AccountSetting, String> {

    Optional<AccountSetting> findByAccountTgId(long telegramId);
}
