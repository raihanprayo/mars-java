package dev.scaraz.mars.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Slf4j
@RequiredArgsConstructor

@ControllerAdvice
public class ExceptionHandling implements ProblemHandling {

}
