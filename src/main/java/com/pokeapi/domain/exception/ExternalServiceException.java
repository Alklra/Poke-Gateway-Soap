package com.pokeapi.domain.exception;

public class ExternalServiceException extends BusinessException {
    private static final String CODE = "EXTERNAL_SERVICE_ERROR";
    
    public ExternalServiceException(String service, String details) {
        super(CODE, String.format("Error calling external service %s: %s", service, details));
    }
}