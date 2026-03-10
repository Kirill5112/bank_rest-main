package com.example.bankcards.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problem.setTitle("Validation Error");
        problem.setInstance(URI.create(request.getDescription(false)));
        return problem;
    }

    @ExceptionHandler(AppBusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessErrors(
            AppBusinessException ex, WebRequest request) {
        String exName = ex.getClass().getSimpleName();
        int status = switch (exName) {
            case "IncorrectPhoneNumberException", "NegativeBalanceException" -> 400;
            case "AuthenticationMismatchException", "BlockingNotOwnCardException",
                 "IllegalTransferException", "AccessDeniedException" -> 403;
            case "IllegalBalanceChangeException", "ReducingExpireException" -> 409;
            case "ResourceNotFoundException" -> 404;
            default -> 400;
        };

        String errorType = switch (exName) {
            case "IncorrectPhoneNumberException", "NegativeBalanceException" -> "Validation Error";
            case "AuthenticationMismatchException", "BlockingNotOwnCardException",
                 "IllegalTransferException", "AccessDeniedException" -> "Access Denied";
            case "IllegalBalanceChangeException", "ReducingExpireException" -> "Conflict";
            case "ResourceNotFoundException" -> "Not Found";
            default -> "Business Error";
        };

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), ex.getMessage());
        problem.setTitle(errorType);
        problem.setInstance(URI.create(request.getDescription(false)));
        return ResponseEntity.status(status).body(problem);
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, WebRequest request) {
        log.error("Unhandled: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setInstance(URI.create(request.getDescription(false)));
        return problem;
    }
}
