package com.yeditepe.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Genellikle unique constraint (kullanıcı adı veya e-posta çakışması)
        // hatalarını yakalarız
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Hata: Bu kullanıcı adı veya e-posta adresi zaten kullanımda.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Beklenmedik bir hata oluştu: " + ex.getMessage());
    }
}
