package com.alquileres.api.controller;

import com.alquileres.api.controller.models.*;
import controlador.Controlador;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import views.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin
public class Controller {

    private Controlador controlador;
    private StorageService storageService;

    public Controller(Controlador controlador, StorageService storageService) {
        this.controlador = controlador;
        this.storageService = storageService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {

        Date date = new Date();
        return date.toString();
    }

    @GetMapping("edificios")
    public List<EdificioView> getEdificios(@RequestHeader("X-Custom-Documento") String documento, @RequestHeader("X-Custom-TipoUsuario") TipoUsuario tipoUsuario) {
        if (tipoUsuario.equals(TipoUsuario.administrador)) {
            return this.controlador.getEdificios();
        }
        if (tipoUsuario.equals(TipoUsuario.duenio))
        return this.controlador.getEdificiosPorDuenio(documento);

        return this.controlador.getEdificiosPorInquilino(documento);
    }

    @GetMapping("edificios/{id}")
    public EdificioView getEdificioById(@PathVariable int id) {
        return this.controlador.buscarEdificioPorCodigo(id);
    }

    @GetMapping("edificios/{id}/unidades")
    public List<UnidadView> getUnidadesByEdificio(@PathVariable int id, @RequestHeader("X-Custom-Documento") String documento, @RequestHeader("X-Custom-TipoUsuario") TipoUsuario tipoUsuario) {
        if (tipoUsuario.equals(TipoUsuario.administrador)) {
            return this.controlador.getUnidadesPorEdificio(id);
        }

        return this.controlador.getUnidadesPorEdificioYPersona(id, documento);
    }

    @GetMapping("edificios/{id}/habitantes")
    public List<PersonaView> getHabitantesByEdificio(@PathVariable int id) {
        return this.controlador.habitantesPorEdificio(id);
    }

    @GetMapping("edificios/{id}/duenios")
    public Set<PersonaView> getDueniosByEdificio(@PathVariable int id) {
        return this.controlador.dueniosPorEdificio(id);
    }

    @GetMapping("edificios/{id}/habilitados")
    public List<PersonaView> getHabilitadosByEdificio(@PathVariable int id) {
        return this.controlador.habilitadosPorEdificio(id);
    }

    @GetMapping("edificios/{id}/reclamos")
    public List<ReclamoView> getReclamosByEdificio(@PathVariable int id) {
        return this.controlador.reclamosPorEdificio(id);
    }

    @GetMapping("reclamos")
    public List<ReclamoView> getReclamos(@RequestHeader("X-Custom-Documento") String documento, @RequestHeader("X-Custom-TipoUsuario") TipoUsuario tipoUsuario) {
        if(tipoUsuario.equals(TipoUsuario.administrador)){
            return this.controlador.getReclamos();
        }
        return this.controlador.reclamosPorPersona(documento);
    }

    @PostMapping("reclamos/generar")
    public ResponseEntity<Void> addReclamo(@RequestHeader("X-Custom-TipoUsuario") TipoUsuario tipoUsuario, @RequestBody GenerarReclamoBody body, @RequestHeader("X-Custom-Documento") String documento) {
        if (body.piso != "" && body.numero != "") {
            UnidadView unidad = this.controlador.getUnidad(body.edificio, body.piso, body.numero);

            if (unidad.isHabitado() && tipoUsuario.equals(TipoUsuario.duenio))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            if (!unidad.isHabitado() && !tipoUsuario.equals(TipoUsuario.duenio))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            this.controlador.agregarReclamoAUnidad(body.edificio, body.piso, body.numero, documento, body.descripcion);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        this.controlador.agregarReclamoAEdificio(body.edificio, documento, body.ubicacion, body.descripcion);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("reclamos/{numero}/cambiarEstado/{estado}")
    public void cambiarEstadoReclamo(@PathVariable int numero, @PathVariable String estado) {
        this.controlador.cambiarEstado(numero, Estado.valueOf(estado));
    }

    @GetMapping("reclamos/{id}")
    public ReclamoView getReclamoById(@PathVariable int id) {
        return this.controlador.reclamosPorNumero(id);
    }

    @GetMapping("reclamos/{id}/imagenes")
    public List<Integer> getImagenesByReclamo(@PathVariable int id) {
        return this.controlador.getImagenByReclamo(id).stream().map(ImagenView::getNumero).collect(Collectors.toList());
    }

    @GetMapping("reclamos/{id}/imagenes/{idImagen}")
    public ResponseEntity getImagenesByReclamo(@PathVariable int id, @PathVariable int idImagen) throws FileNotFoundException {
        ImagenView imagen = this.controlador.getImagen(idImagen);
        File fileImagen = storageService.load(imagen.getDireccion());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", imagen.getTipo());
        InputStreamResource body = new InputStreamResource(new FileInputStream(fileImagen));
        return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("reclamos/{numero}/imagenes")
    public void agregarImagen(@RequestParam("file") MultipartFile file, @PathVariable int numero) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ArchivoInvalido(file.getContentType());
        }
        String nombreFisicoDelArchivo = storageService.store(file);
        this.controlador.agregarImagenAReclamo(numero, nombreFisicoDelArchivo, file.getContentType());
    }

    @GetMapping("personas")
    public List<PersonaView> getPersonas() {
        return this.controlador.getPersonas();
    }

    @GetMapping("personas/{documento}")
    public PersonaView getPersona(@PathVariable String documento) {
        return this.controlador.getPersona(documento);
    }

    @PostMapping("personas/agregarPersona")
    public PersonaView addPersona(@RequestBody AgregarPersonaBody body) {
        return this.controlador.agregarPersona(body.documento, body.nombre, TipoUsuario.valueOf(body.tipo), body.pass);
    }

    @DeleteMapping("personas/{id}")
    public void deletePersona(@PathVariable String id) {
        this.controlador.eliminarPersona(id);
    }

    @GetMapping("personas/{documento}/reclamos")
    public List<ReclamoView> getReclamoByPersona(@PathVariable String documento) {
        return this.controlador.reclamosPorPersona(documento);
    }

    @GetMapping("unidades/{id}")
    public UnidadView getUnidadById(@PathVariable int id) {
        return this.controlador.getUnidadById(id);
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/reclamos")
    public List<ReclamoView> getReclamoByUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero) {
        return this.controlador.reclamosPorUnidad(codigo, piso, numero);
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/duenios")
    public List<PersonaView> getDueniosByUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero) {
        return this.controlador.dueniosPorUnidad(codigo, piso, numero);
    }

    @GetMapping("unidades/{codigo}/{piso}/{numero}/inquilinos")
    public List<PersonaView> getInquilinosByUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero) {
        return this.controlador.inquilinosPorUnidad(codigo, piso, numero);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/transferir")
    public void transferirUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @RequestBody BodyDocumento body) {
        this.controlador.transferirUnidad(codigo, piso, numero, body.documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/agregarDuenio")
    public void addDuenio(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @RequestBody BodyDocumento body) {
        this.controlador.agregarDuenioUnidad(codigo, piso, numero, body.documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/agregarInquilino")
    public void addInquilino(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @RequestBody BodyDocumento body) {
        this.controlador.agregarInquilinoUnidad(codigo, piso, numero, body.documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/alquilar")
    public void alquilarUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero, @RequestBody BodyDocumento body) {
        this.controlador.alquilarUnidad(codigo, piso, numero, body.documento);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/habitar")
    public void habitar(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero) {
        this.controlador.habitarUnidad(codigo, piso, numero);
    }

    @PostMapping("unidades/{codigo}/{piso}/{numero}/liberar")
    public void liberarUnidad(@PathVariable int codigo, @PathVariable String piso, @PathVariable String numero) {
        this.controlador.liberarUnidad(codigo, piso, numero);
    }

    @PostMapping("login")
    public UsuarioDto login(@RequestBody LoginRequest body) {
        PersonaView persona = this.controlador.logIn(body.documento, body.password);
        return new UsuarioDto(persona);
    }
}