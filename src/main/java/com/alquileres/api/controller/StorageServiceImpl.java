package com.alquileres.api.controller;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.TemporalField;

public class StorageServiceImpl implements StorageService {

    final private PropiedadesDeAplicacion propiedadesDeAplicacion;

    public StorageServiceImpl(PropiedadesDeAplicacion propiedadesDeAplicacion) {
        this.propiedadesDeAplicacion = propiedadesDeAplicacion;
    }

    @Override
    public String store(MultipartFile file) {
        String fileName = String.valueOf(Instant.now().toEpochMilli());
        Path path = FileSystems.getDefault().getPath(propiedadesDeAplicacion.getImagenes().getHome(), fileName);

        try {
            file.transferTo(path);
            return fileName;
        } catch (IOException e) {
            throw new UploadException(e);
        }
    }

    @Override
    public File load(String fileName) {
        Path path = FileSystems.getDefault().getPath(propiedadesDeAplicacion.getImagenes().getHome(), fileName);

        return path.toFile();
    }

}
