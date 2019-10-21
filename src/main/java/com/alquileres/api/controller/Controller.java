package com.alquileres.api.controller;

import controlador.Controlador;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import views.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
public class Controller {

    private Controlador controlador;
    private StorageService storageService;

    public Controller(Controlador controlador, StorageService storageService){
        this.controlador = controlador;
        this.storageService = storageService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {

        Date date = new Date();
        return date.toString();
    }

    @GetMapping("edificios")
    public List<EdificioView> getEdificios(){
        List<EdificioView> edificios = this.controlador.getEdificios();
        return edificios;
    }

    @GetMapping("edificios/{id}/unidades")
//    @RequestMapping(value = "/edificios/{id}/unidades", method = RequestMethod.GET, produces = {"application/json"})
    public List<UnidadView> getUnidadesByEdificio(@PathVariable int id){
        List<UnidadView> unidades = this.controlador.getUnidadesPorEdificio(id);
        return unidades;
    }

    @GetMapping("edificios/{id}/habitantes")
    public List<PersonaView> getHabitantesByEdificio(@PathVariable int id){
        List<PersonaView> habitantes = this.controlador.habitantesPorEdificio(id);
        return habitantes;
    }

    @GetMapping("edificios/{id}/duenios")
    public Set<PersonaView> getDueniosByEdificio(@PathVariable int id){
        Set<PersonaView> duenios = this.controlador.dueniosPorEdificio(id);
        return duenios;
    }

    @GetMapping("edificios/{id}/habilitados")
    public List<PersonaView> getHabilitadosByEdificio(@PathVariable int id){
        List<PersonaView> habilitados = this.controlador.habilitadosPorEdificio(id);
        return habilitados;
    }

    @GetMapping("edificios/{id}/reclamos")
    public List<ReclamoView> getReclamosByEdificio(@PathVariable int id){
        List<ReclamoView> reclamos = this.controlador.reclamosPorEdificio(id);
        return reclamos;
    }

    @PostMapping("reclamos/{codigo}/{piso}/{numero}/{documento}/{ubicacion}/{descripcion}")
    public void addReclamo(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @PathVariable String documento, @PathVariable String ubicacion, @PathVariable String descripcion){
        this.controlador.agregarReclamo(codigo,piso,numero,documento,ubicacion,descripcion);
    }

    @PatchMapping("reclamos/{numero}/cambiarEstado/{estado}")
    public void cambiarEstadoReclamo(@PathVariable int numero, @PathVariable String estado){
        this.controlador.cambiarEstado(numero, Estado.valueOf(estado));
    }

    @GetMapping("reclamos/{id}")
    public ReclamoView getReclamoById(@PathVariable int id){
        ReclamoView reclamo = this.controlador.reclamosPorNumero(id);
        return reclamo;
    }

    @PostMapping("reclamos/{numero}/imagenes")
    public void agregarImagen(@RequestParam("file") MultipartFile file, @PathVariable int numero){
        if(!file.getContentType().startsWith("image/")){
            throw new ArchivoInvalido(file.getContentType());
        }
        String nombreFisicoDelArchivo = storageService.store(file);
        this.controlador.agregarImagenAReclamo(numero,nombreFisicoDelArchivo, file.getContentType());
    }

    @PostMapping("personas/{nombre}/{documento}")
    public void addPersona(@PathVariable String nombre, @PathVariable String documento){
        this.controlador.agregarPersona(documento, nombre);
    }

    @DeleteMapping("personas/{documento}")
    public void deletePersona(@PathVariable String documento){
        this.controlador.eliminarPersona(documento);
    }

    @GetMapping("personas/{documento}/reclamos")
    public List<ReclamoView> getReclamoByPersona(@PathVariable String documento){
        List<ReclamoView> reclamos = this.controlador.reclamosPorPersona(documento);
        return reclamos;
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/reclamos")
    public List<ReclamoView> getReclamoByUnidad(@PathVariable int codigo ,@PathVariable String piso, @PathVariable String numero){
        List<ReclamoView> reclamos = this.controlador.reclamosPorUnidad(codigo,piso,numero);
        return reclamos;
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/duenios")
    public List<PersonaView> getDueniosByUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero){
        List<PersonaView> duenios = this.controlador.dueniosPorUnidad(codigo, piso, numero);
        return duenios;
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/inquilinos")
    public List<PersonaView> getInquilinosByUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero){
        List<PersonaView> inquilinosPorUnidad = this.controlador.inquilinosPorUnidad(codigo, piso, numero);
        return inquilinosPorUnidad;
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/transferir/{documento}")
    public void transferirUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @PathVariable String documento){
        this.controlador.transferirUnidad(codigo, piso, numero,documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/agregarDuenio/{documento}")
    public void addDuenio(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @PathVariable String documento){
        this.controlador.agregarDuenioUnidad(codigo, piso, numero,documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/agregarInquilino/{documento}")
    public void addInquilino(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @PathVariable String documento){
        this.controlador.agregarInquilinoUnidad(codigo, piso, numero,documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/alquilar/{documento}")
    public void alquilarUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @PathVariable String documento){
        this.controlador.alquilarUnidad(codigo, piso, numero,documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/habitar")
    public void habitar(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero){
        this.controlador.habitarUnidad(codigo, piso, numero);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/liberar")
    public void liberarUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero){
        this.controlador.liberarUnidad(codigo, piso, numero);
    }

}
