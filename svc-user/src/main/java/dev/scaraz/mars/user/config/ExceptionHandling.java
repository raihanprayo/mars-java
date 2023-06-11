package dev.scaraz.mars.user.config;

import dev.scaraz.mars.security.MarsExceptionHandling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@RequiredArgsConstructor

@ControllerAdvice
public class ExceptionHandling extends MarsExceptionHandling {
}
