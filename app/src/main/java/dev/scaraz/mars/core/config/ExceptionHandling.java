package dev.scaraz.mars.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

@Slf4j
@RequiredArgsConstructor

@ControllerAdvice
public class ExceptionHandling implements ProblemHandling,
        GeneralAdviceTrait,
        SecurityAdviceTrait,
        IOAdviceTrait,
        RoutingAdviceTrait,
        ValidationAdviceTrait {

    @Override
    public boolean isCausalChainsEnabled() {
        return false;
    }

    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request, HttpStatus status) {
        throwable.printStackTrace();
        ProblemHandling.super.log(throwable, problem, request, status);
    }
}
