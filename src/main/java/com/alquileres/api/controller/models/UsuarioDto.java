package com.alquileres.api.controller.models;

import views.PersonaView;

public class UsuarioDto {
    private final String tipoUsuario;
    private final String documento;

    public UsuarioDto(PersonaView persona) {
        this.tipoUsuario = persona.getTipo().name();
        this.documento = persona.getDocumento();
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getDocumento() {
        return documento;
    }
}
