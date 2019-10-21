package com.alquileres.api.controller;

public class ApiException extends RuntimeException {
    public ApiException(Exception e) {
        super (e);
    }

    public ApiException(String causa) {
        super(causa);
    }
}
