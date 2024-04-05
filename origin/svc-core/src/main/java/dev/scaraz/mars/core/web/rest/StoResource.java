package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.general.StoDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.StoQueryService;
import dev.scaraz.mars.core.query.criteria.StoCriteria;
import dev.scaraz.mars.core.service.order.StoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/sto")
public class StoResource {

    private final StoService service;
    private final StoQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(StoCriteria criteria, Pageable pageable) {
        Page<Sto> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/sto");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Sto sto) {
        Sto result = service.create(sto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping(path = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFromCsv(@RequestParam("csv") MultipartFile file) throws IOException {
        List<String> acceptableContentType = List.of("application/csv", "text/csv");

        if (!acceptableContentType.contains(file.getContentType()))
            throw BadRequestException.args("invalid upload file content type");

        List<Sto> stos = service.createFromFile(file);
        return new ResponseEntity<>(stos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable int id,
                                        @RequestBody StoDTO updateDTO) {
        Sto update = service.update(id, updateDTO);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteById(@RequestBody List<Integer> ids) {
        service.deleteBulkById(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/template/csv")
    public ResponseEntity<?> getCsvTemplate() {
        List<List<String>> csv = new ArrayList<>(List.of(
                List.of("kode", "nama", "witel", "datel"),
                List.of("BRS", "BAROS", "BANTEN", "BANTEN")
        ));

        String content = csv.stream()
                .map(line -> String.join(";", line))
                .collect(Collectors.joining("\n"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/csv"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("template_sto.csv")
                .build());

        ByteArrayResource resource = new ByteArrayResource(content.getBytes());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}
