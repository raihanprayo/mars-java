package dev.scaraz.mars.user.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@PreAuthorize("hasAnyRole('admin')")
@RestController
@RequestMapping("/credential")
public class UserResource {
}
