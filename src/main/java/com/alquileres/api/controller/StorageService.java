package com.alquileres.api.controller;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file);

//    Path load(String filename);

}