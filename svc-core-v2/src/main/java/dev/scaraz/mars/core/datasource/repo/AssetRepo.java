package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.core.datasource.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Long> {
}
