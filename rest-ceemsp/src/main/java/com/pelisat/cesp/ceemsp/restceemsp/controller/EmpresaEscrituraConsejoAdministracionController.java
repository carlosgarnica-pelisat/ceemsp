package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraConsejoRepository;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraApoderadoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraConsejoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraConsejoAdministracionController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CONSEJO_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/consejos";
    private final EmpresaEscrituraConsejoService empresaEscrituraConsejoService;

    @Autowired
    public EmpresaEscrituraConsejoAdministracionController(EmpresaEscrituraConsejoService empresaEscrituraConsejoService,
                                               JwtUtils jwtUtils) {
        this.empresaEscrituraConsejoService = empresaEscrituraConsejoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraConsejoService.obtenerConsejosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI + "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraConsejoDto> obtenerTodosConsejosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraConsejoService.obtenerTodosConsejosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto obtenerEscrituraConsejoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String socioUuid
    ) {
        return null;
        //return empresaEscrituraApoderadoService.obtenerRepresentantePorUuid(empresaUuid, escrituraUuid, socioUuid, false);
    }

    @PostMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraConsejoDto crearEscrituraConsejo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraConsejoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.crearConsejo(empresaUuid, escrituraUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraConsejoDto modificarEscrituraConsejo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "consejoUuid") String consejoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraConsejoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.actualizarConsejo(empresaUuid, escrituraUuid, consejoUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaEscrituraConsejoDto eliminarEscrituraConsejo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "consejoUuid") String consejoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("consejo") String consejo,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.eliminarConsejo(empresaUuid, escrituraUuid, consejoUuid, username, new Gson().fromJson(consejo, EmpresaEscrituraConsejoDto.class), archivo);
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}/documentos/fundatorios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> obtenerDocumentoFundatorio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "consejoUuid") String consejoUuid
    ) throws Exception {
        File file = empresaEscrituraConsejoService.obtenerDocumentoFundatorioBajaConsejo(empresaUuid, escrituraUuid, consejoUuid);
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
}
