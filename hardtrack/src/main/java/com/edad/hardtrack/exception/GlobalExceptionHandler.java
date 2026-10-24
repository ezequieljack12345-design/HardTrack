package com.edad.hardtrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES
 * 
 * @ControllerAdvice intercepta excepciones lanzadas por controladores/servicios
 * y devuelve respuestas HTTP uniformes con estructura ErrorResponse.
 * 
 * MAPEADO:
 * - DeviceNotFoundException/DeviceTypeNotFoundException -> 404 NOT_FOUND
 * - DeviceNotAvailableException -> 409 CONFLICT (dispositivo ya no disponible)
 * - InvalidOrderException -> 400 BAD_REQUEST (datos inválidos)
 * - Exception genérica -> 500 INTERNAL_SERVER_ERROR
 * 
 * Ver: README_HARDTRACK_DETALLADO.md sección 10
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeviceNotFound(DeviceNotFoundException exc) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exc.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeviceTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeviceTypeNotFound(DeviceTypeNotFoundException exc) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exc.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeviceNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleDeviceNotAvailable(DeviceNotAvailableException exc) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), exc.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrder(InvalidOrderException exc) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exc.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exc) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exc.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
