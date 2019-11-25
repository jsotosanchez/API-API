package com.alquileres.api.controller;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {

    String store(MultipartFile file);

    File load(String filename);

}