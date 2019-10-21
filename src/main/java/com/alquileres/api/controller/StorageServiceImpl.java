package com.alquileres.api.controller;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;

public class StorageServiceImpl implements StorageService {

    final private PropiedadesDeAplicacion propiedadesDeAplicacion;

    public StorageServiceImpl(PropiedadesDeAplicacion propiedadesDeAplicacion) {
        this.propiedadesDeAplicacion = propiedadesDeAplicacion;
    }

    @Override
    public String store(MultipartFile file) {
        String fileName = Instant.now().toString();
        Path path = FileSystems.getDefault().getPath(propiedadesDeAplicacion.getImagenes().getHome(), fileName);

        try {
            file.transferTo(path);
            return fileName;
        } catch (IOException e) {
            throw new UploadException(e);
        }
    }

//    @Override
//    public Path load(String filename) {
//        return null;
//    }

}
