package dev.scaraz.mars.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

public class MarsExceptionHandling implements ProblemHandling, SecurityAdviceTrait {

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
