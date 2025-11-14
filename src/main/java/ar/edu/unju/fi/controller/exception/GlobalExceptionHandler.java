package ar.edu.unju.fi.controller.exception;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MensajeError> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError("JSON mal formado o tipos de datos inválidos."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeError> manejarValidacionesBody(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Errores de validación: ");
        boolean primero = true;
        for (var error : ex.getBindingResult().getFieldErrors()) {
            if (!primero) sb.append("; ");
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage());
            primero = false;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError(sb.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MensajeError> manejarConstraintViolations(ConstraintViolationException ex) {
        StringBuilder sb = new StringBuilder("Errores de validación: ");
        boolean primero = true;
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            if (!primero) sb.append("; ");
            sb.append(v.getPropertyPath()).append(": ").append(v.getMessage());
            primero = false;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError(sb.toString()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MensajeError> manejarRuntime(RuntimeException ex) {
        MensajeError error = new MensajeError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MensajeError> manejarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeError> manejarGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeError("Error interno del servidor."));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleResourceNotFound(ResourceNotFoundException ex) {
        return Map.of(
                "error", "Recurso no encontrado",
                "mensaje", ex.getMessage()
        );
    }
}
