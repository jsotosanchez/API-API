package com.alquileres.api.controller;

public class ArchivoInvalido extends UploadException{
    public ArchivoInvalido(String contentType) {
        super(contentType);
    }
}
