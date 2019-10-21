package com.alquileres.api.controller;

public class PropiedadesDeAplicacion {
    private PropiedadesDeImagenes imagenes;

    public void setImagenes(PropiedadesDeImagenes imagenes) {
        this.imagenes = imagenes;
    }

    public PropiedadesDeImagenes getImagenes() {
        return imagenes;
    }

    public static class PropiedadesDeImagenes {
        private String home;

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }
    }
}
