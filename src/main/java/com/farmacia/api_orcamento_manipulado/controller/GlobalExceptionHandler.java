package com.farmacia.api_orcamento_manipulado.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String path = request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : request.getDescription(false);
        logger.warn("Bad request [{}] {} - {}", correlationId, path, ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "BadRequest");
        body.put("message", ex.getMessage());
        body.put("correlationId", correlationId);
        body.put("path", path);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String path = request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : request.getDescription(false);
        logger.error("Unhandled exception [{}] processing request {}", correlationId, path, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "InternalServerError");
        body.put("message", ex.getMessage());
        body.put("correlationId", correlationId);
        body.put("path", path);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
