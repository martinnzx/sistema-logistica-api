package ar.edu.unju.fi.controller.exception;

import ar.edu.unju.fi.controller.dto.MensajeError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MensajeError> manejarIllegalArgument(IllegalArgumentException ex) {
        MensajeError error = new MensajeError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
