package com.fondocesantia.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler using BDD Given-When-Then pattern.
 */
@DisplayName("GlobalExceptionHandler - Tests de Excepciones")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Given una excepción IllegalArgumentException, When se maneja, Then retorna código 400")
    void testHandleIllegalArgumentException() {
        // Given: Una excepción de argumento ilegal
        IllegalArgumentException ex = new IllegalArgumentException("Mensaje de error");

        // When: Se maneja la excepción
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        // Then: Retorna código 400
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Mensaje de error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Given una excepción IllegalStateException, When se maneja, Then retorna código 409")
    void testHandleIllegalStateException() {
        // Given: Una excepción de estado ilegal
        IllegalStateException ex = new IllegalStateException("Estado inválido");

        // When: Se maneja la excepción
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalStateException(ex);

        // Then: Retorna código 409
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Estado inválido", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Given una excepción genérica, When se maneja, Then retorna código 500")
    void testHandleGenericException() {
        // Given: Una excepción genérica
        Exception ex = new RuntimeException("Error interno");

        // When: Se maneja la excepción
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        // Then: Retorna código 500
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Given un ErrorResponse, When se crea, Then contiene timestamp")
    void testErrorResponseHasTimestamp() {
        // Given: Un ErrorResponse
        Map<String, String> errores = new HashMap<>();
        errores.put("campo", "error");

        // When: Se crea la respuesta
        GlobalExceptionHandler.ErrorResponse response = new GlobalExceptionHandler.ErrorResponse(
                400, "Mensaje", errores);

        // Then: Contiene timestamp
        assertNotNull(response.getTimestamp());
    }
}