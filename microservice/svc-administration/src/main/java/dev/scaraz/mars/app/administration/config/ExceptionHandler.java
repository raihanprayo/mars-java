package dev.scaraz.mars.app.administration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.AdviceTrait;
import org.zalando.problem.spring.web.advice.general.ProblemAdviceTrait;

@Slf4j
@ControllerAdvice
public class ExceptionHandler implements AdviceTrait, ProblemAdviceTrait {

    @Override
    public boolean isCausalChainsEnabled() {
        return false;
    }


    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request, HttpStatus status) {
        log.error("Error - ", throwable);
        ProblemAdviceTrait.super.log(throwable, problem, request, status);
    }
}
