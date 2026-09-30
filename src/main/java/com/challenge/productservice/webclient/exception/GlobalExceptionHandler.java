package com.challenge.productservice.webclient.exception;

import com.challenge.productservice.webclient.model.ApiError;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ApiError> notFound(ProductNotFoundException ex, ServerWebExchange exchange) {
        return Mono.just(error(404, "Not Found", ex.getMessage(), exchange, Map.of()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiError> validation(WebExchangeBindException ex, ServerWebExchange exchange) {
        var details = ex.getFieldErrors().stream().collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage() == null ? "Invalid value" : e.getDefaultMessage(), (a, b) -> a));
        return Mono.just(error(400, "Bad Request", "Request validation failed", exchange, details));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiError> badRequest(IllegalArgumentException ex, ServerWebExchange exchange) {
        return Mono.just(error(400, "Bad Request", ex.getMessage(), exchange, Map.of()));
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ApiError> persistence(DataAccessException ex, ServerWebExchange exchange) {
        return Mono.just(error(500, "Internal Server Error", "Persistence operation failed", exchange, Map.of()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ApiError> unexpected(Exception ex, ServerWebExchange exchange) {
        return Mono.just(error(500, "Internal Server Error", "An unexpected error occurred", exchange, Map.of()));
    }

    private ApiError error(int status, String label, String message, ServerWebExchange exchange, Map<String, String> details) {
        return new ApiError(Instant.now(), status, label, message, exchange.getRequest().getPath().value(), details);
    }
}
