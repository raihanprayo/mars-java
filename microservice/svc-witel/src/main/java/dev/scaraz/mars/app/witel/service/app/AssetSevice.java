package dev.scaraz.mars.app.witel.service.app;

import dev.scaraz.mars.app.witel.domain.Asset;
import dev.scaraz.mars.app.witel.repository.AssetRepo;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssetSevice {

    private final AssetRepo repo;

    public Asset save(MultipartFile mf) throws IOException {
        InputStream is = mf.getInputStream();
        byte[] content = is.readAllBytes();
        String filename = Objects.requireNonNullElse(mf.getOriginalFilename(), mf.getName());
        return save(filename, mf.getContentType(), content);
    }

    public Asset save(String filename, String contentType, byte[] content) {
        return repo.save(repo.findByName(filename)
                .map(asset -> asset.toBuilder()
                        .name(filename)
                        .content(content)
                        .contentType(contentType)
                        .build())
                .orElseGet(() -> Asset.builder()
                        .name(filename)
                        .content(content)
                        .contentType(contentType)
                        .build()));
    }

    public Asset get(String assetId) {
        return repo.findByIdOrName(assetId, assetId)
                .orElseThrow(() -> NotFoundException.entity(Asset.class, "id/filename", assetId));
    }

}
