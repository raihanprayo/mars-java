package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.app.administration.service.app.RoleService;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/role")
public class RoleResource {


    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(
                roleService.findAll(),
                HttpStatus.OK
        );
    }

    @GetMapping("/witel/{witel}")
    public ResponseEntity<?> findByWitel(@PathVariable Witel witel) {
        return new ResponseEntity<>(
                roleService.findByWitel(witel),
                HttpStatus.OK
        );
    }

}
