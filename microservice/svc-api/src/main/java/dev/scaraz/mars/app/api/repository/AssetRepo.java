package dev.scaraz.mars.app.api.repository;

import dev.scaraz.mars.app.api.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepo extends JpaRepository<Asset, String> {
}
