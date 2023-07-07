package dev.scaraz.mars.v1.admin.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@Slf4j
@RequiredArgsConstructor

@ControllerAdvice
public class ExceptionHandling implements ProblemHandling, SecurityAdviceTrait {

    @Override
    public boolean isCausalChainsEnabled() {
        return false;
    }

    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request, HttpStatus status) {
        log.debug("Poblem Occured");
        throwable.printStackTrace();
        ProblemHandling.super.log(throwable, problem, request, status);
    }
}
