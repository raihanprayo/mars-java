package dev.scaraz.mars.app.witel.repository;

import dev.scaraz.mars.app.witel.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepo extends JpaRepository<Asset, String> {
    Optional<Asset> findByName(String filename);
    Optional<Asset> findByIdOrName(String id, String filename);
}
