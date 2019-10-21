package com.alquileres.api.controller;

import java.io.IOException;

public class UploadException extends ApiException {
    public UploadException(IOException e) {
        super(e);
    }

    public UploadException(String causa) {
        super(causa);
    }
}
