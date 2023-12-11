package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalCanDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalVehiculoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaPersonalService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaPersonalController {
    private final EmpresaPersonalService personaService;
    private final JwtUtils jwtUtils;
    private static final String PERSONA_URI = "/personas";

    @Autowired
    public EmpresaPersonalController(EmpresaPersonalService personaService, JwtUtils jwtUtils) {
        this.personaService = personaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerPersonal(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.obtenerTodos(username);
    }

    @GetMapping(value = PERSONA_URI + "/no-asignados", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerPersonalNoAsignado(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.obtenerSinAsignar(username);
    }

    @GetMapping(value = PERSONA_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto obtenerPersonaPorUuid(
            @PathVariable(value = "personaUuid") String personaUuid
    ) {
        return personaService.obtenerPorUuid(personaUuid);
    }

    @PostMapping(value = PERSONA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto guardarPersona(
            HttpServletRequest request,
            @RequestBody PersonaDto personalDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.crearNuevo(personalDto, username);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto modificarPersona(
            HttpServletRequest request,
            @RequestBody PersonaDto personaDto,
            @PathVariable(value = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.modificarPersona(personaUuid, username, personaDto);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonaDto eliminarPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("persona") String persona
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.eliminarPersona(personaUuid, username, new Gson().fromJson(persona, PersonaDto.class), archivo);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/puestos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonaDto modificarPuestoTrabajo(
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("persona") String persona,
            @PathVariable(value = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.modificarInformacionPuesto(new Gson().fromJson(persona, PersonaDto.class), username, personaUuid, archivo);
    }

    @PostMapping(value = PERSONA_URI + "/{personaUuid}/canes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void asignarCanAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalCanDto personalCanDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.asignarCanAPersona(personaUuid, personalCanDto, username);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/canes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void desasignarCanAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalCanDto personalCanDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.desasignarCanAPersona(personaUuid, personalCanDto, username);
    }

    @PostMapping(value = PERSONA_URI + "/{personaUuid}/vehiculos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void asignarVehiculoAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalVehiculoDto personalVehiculoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.asignarVehiculoAPersona(personaUuid, personalVehiculoDto, username);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/vehiculos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void desasignarVehiculoAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalVehiculoDto personalVehiculoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.desasignarVehiculoAPersona(personaUuid, personalVehiculoDto, username);
    }

    @PostMapping(value = PERSONA_URI + "/{personaUuid}/armas/cortas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void asignarArmaCortaAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalArmaDto personalArmaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.asignarArmaCortaAPersona(personaUuid, personalArmaDto, username);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/armas/cortas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void desasignarArmaCortaAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalArmaDto personalArmaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.desasignarArmaCortaAPersona(personaUuid, personalArmaDto, username);
    }

    @PostMapping(value = PERSONA_URI + "/{personaUuid}/armas/largas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void asignarArmaLargaAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalArmaDto personalArmaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.asignarArmaLargaAPersona(personaUuid, personalArmaDto, username);
    }

    @GetMapping(value = PERSONA_URI + "/{personaUuid}/volante", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarVolanteCuip(
            @PathVariable(value = "personaUuid") String personaUuid
    ) throws Exception {
        File file = personaService.descargarVolanteCuip(personaUuid);
        HttpHeaders responseHeaders = new HttpHeaders();
        MediaType mediaType = null;

        switch(FilenameUtils.getExtension(file.getName())) {
            case "pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
        }

        responseHeaders.setContentType(mediaType);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PutMapping(value = PERSONA_URI + "/{personaUuid}/armas/largas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void desasignarArmaLargaAPersona(
            HttpServletRequest request,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestBody PersonalArmaDto personalArmaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personaService.desasignarArmaLargaAPersona(personaUuid, personalArmaDto, username);
    }
}
