package dev.scaraz.mars.app.witel.web.rest;

import dev.scaraz.mars.app.witel.domain.Asset;
import dev.scaraz.mars.app.witel.service.app.AssetSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/assets")
public class AssetResource {

    private final AssetSevice assetSevice;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Asset asset = assetSevice.get(id);

        String contentType = StringUtils.isNotBlank(asset.getContentType()) ? asset.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        ByteArrayResource resource = new ByteArrayResource(asset.getContent());
        return ResponseEntity.ok()
                .contentType(new MediaType(contentType))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(asset.getName())
                        .build()
                        .toString())
                .build();
    }

}
