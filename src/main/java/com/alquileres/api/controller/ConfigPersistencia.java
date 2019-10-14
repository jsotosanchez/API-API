package com.alquileres.api.controller;

import controlador.Controlador;
import daos.EdificioDAO;
import daos.PersonaDAO;
import daos.ReclamoDAO;
import daos.UnidadDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigPersistencia {

    @Bean
    Controlador controlador(EdificioDAO edificioDAO, UnidadDAO unidadDao, PersonaDAO personaDAO, ReclamoDAO reclamoDAO){
        return new Controlador(edificioDAO, unidadDao, personaDAO, reclamoDAO);
    }

    @Bean
    EdificioDAO edificioDAO(){
        return new EdificioDAO(personaDAO(),unidadDAO(personaDAO()));
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
}
