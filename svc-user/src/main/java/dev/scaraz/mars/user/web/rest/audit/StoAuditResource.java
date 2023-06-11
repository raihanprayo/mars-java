package dev.scaraz.mars.user.web.rest.audit;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.mapper.StoMapper;
import dev.scaraz.mars.user.service.StoService;
import dev.scaraz.mars.user.web.dto.StoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sto/audit")
@PreAuthorize("hasRole('" + AppConstants.Authority.ADMIN_ROLE + "')")
public class StoAuditResource {

    private final StoMapper mapper;
    private final StoService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody StoDTO req) {
        return new ResponseEntity<>(mapper.toDTO(service.save(mapper.toEntity(req))), HttpStatus.CREATED);
    }

    @PostMapping("/import/csv")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        List<String> csvMimeTypes = List.of(AppConstants.MimeType.APPLICATION_CSV_VALUE, "text/csv");

        if (contentType == null || !csvMimeTypes.contains(contentType))
            throw new BadRequestException("Invalid content-type");

        List<StoDTO> stos = service.importCsv(file.getInputStream()).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(stos, HttpStatus.CREATED);
    }

}
