package dev.scaraz.mars.app.administration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.AdviceTrait;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Slf4j
@ControllerAdvice
public class ExceptionHandling implements AdviceTrait, ProblemHandling {

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
