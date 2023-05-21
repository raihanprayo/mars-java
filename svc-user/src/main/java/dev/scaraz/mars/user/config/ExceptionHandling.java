package dev.scaraz.mars.user.config;

import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.Violation;

import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
        throwable.printStackTrace();
        ProblemHandling.super.log(throwable, problem, request, status);
    }

//    @Override
//    public ResponseEntity<Problem> newConstraintViolationProblem(Throwable throwable,
//                                                                 Collection<Violation> stream,
//                                                                 NativeWebRequest request) {
//
//        List<Violation> violations = stream.stream()
//                // sorting to make tests deterministic
//                .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
//                .map((Violation violation) -> {
//                    if (violation.getMessage().contains("Failed to convert property value of type")) {
//                        return new Violation(violation.getField(), "Please check value " + violation.getField());
//                    }
//                    return violation;
//                })
//                .collect(toList());
//
//        Problem problem = new CustomConstraintViolationProblem(
//                "ERR-FORM-01",
//                MessageTranslator.toLocale("validation.constraints.message"),
//                violations);
//        return create(throwable, problem, request);
//    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleConstraintViolated(final DataIntegrityViolationException exception,
                                                            final NativeWebRequest request) {
        if (exception.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException constraintViolationException =
                    (org.hibernate.exception.ConstraintViolationException) exception.getCause();
            return create(Status.BAD_REQUEST,
                    constraintViolationException.getSQLException(),
                    request);
        }
        return create(Status.INTERNAL_SERVER_ERROR,
                exception,
                request);
    }


    @ExceptionHandler
    public ResponseEntity<Problem> handleEmptyResult(final EmptyResultDataAccessException exception,
                                                     final NativeWebRequest request) {


        return create(Status.NOT_FOUND,
                exception,
                request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleInsufficientAuthentication(final InsufficientAuthenticationException exception,
                                                                    final NativeWebRequest request) {


        return create(Status.FORBIDDEN,
                exception,
                request);

    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleAccessDenied(final AccessDeniedException exception,
                                                      final NativeWebRequest request) {
        return create(Status.UNAUTHORIZED,
                exception,
                request);
    }


    @ExceptionHandler
    public ResponseEntity<Problem> handleAuthenticationException(final AuthenticationServiceException exception,
                                                                                        final NativeWebRequest request) {
        return create(Status.UNAUTHORIZED,
                exception,
                request);
    }

}
