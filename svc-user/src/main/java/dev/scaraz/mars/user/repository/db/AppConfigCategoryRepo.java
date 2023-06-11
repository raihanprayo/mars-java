package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.AppConfigCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigCategoryRepo extends JpaRepository<AppConfigCategory, String> {
}
