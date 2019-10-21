package com.alquileres.api.controller;

import controlador.Controlador;
import daos.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigPersistencia {

    @Bean
    Controlador controlador(EdificioDAO edificioDAO, UnidadDAO unidadDao, PersonaDAO personaDAO, ReclamoDAO reclamoDAO, ImagenDAO imagenDAO){
        return new Controlador(edificioDAO, unidadDao, personaDAO, reclamoDAO, imagenDAO);
    }

    @Bean
    EdificioDAO edificioDAO(){
        return new EdificioDAO(unidadDAO(personaDAO()));
    }

    @Bean
    UnidadDAO unidadDAO(PersonaDAO personaDAO){
        return new UnidadDAO(() -> this.edificioDAO(), personaDAO);
    };

    @Bean
    PersonaDAO personaDAO(){
        return new PersonaDAO();
    }

    @Bean
    ReclamoDAO reclamoDAO(EdificioDAO edificioDAO, PersonaDAO personaDAO, UnidadDAO unidadDAO){
        return new ReclamoDAO(edificioDAO, personaDAO, unidadDAO);
    }

    @Bean
    ImagenDAO imagenDAO(ReclamoDAO reclamoDAO){
        return new ImagenDAO(reclamoDAO);
    }
    @Bean
    @ConfigurationProperties(prefix = "aplicacion")
    PropiedadesDeAplicacion propiedadesDeAplicacion(){
        return new PropiedadesDeAplicacion();
    }

    @Bean
    StorageService storageService(PropiedadesDeAplicacion propiedadesDeAplicacion){
        return new StorageServiceImpl(propiedadesDeAplicacion);
    }
}
