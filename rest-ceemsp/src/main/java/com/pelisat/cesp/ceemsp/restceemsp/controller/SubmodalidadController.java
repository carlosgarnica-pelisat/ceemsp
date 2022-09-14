package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.SubmodalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SubmodalidadController {
    private final SubmodalidadService submodalidadService;
    private final JwtUtils jwtUtils;
    private static final String SUBMODALIDAD_URI = "/catalogos/modalidades/{modalidadUuid}/submodalidades";

    @Autowired
    public SubmodalidadController(SubmodalidadService submodalidadService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.submodalidadService = submodalidadService;
    }

    @GetMapping(value = SUBMODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SubmodalidadDto> obtenerSubmodalidades(
            @PathVariable(value = "modalidadUuid") String modalidadUuid
    ) {
        return submodalidadService.obtenerSubmodalidadesPorModalidadUuid(
                modalidadUuid
        );
    }

    @PostMapping(value = SUBMODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubmodalidadDto guardarSubmodalidad(
            HttpServletRequest request,
            @RequestBody SubmodalidadDto submodalidadDto,
            @PathVariable(value = "modalidadUuid") String modalidadUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return submodalidadService.guardarSubmodalidad(modalidadUuid, username, submodalidadDto);
    }

    @PutMapping(value = SUBMODALIDAD_URI + "/{submodalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubmodalidadDto modificarSubmodalidad(
            HttpServletRequest request,
            @PathVariable(value = "modalidadUuid") String modalidadUuid,
            @PathVariable(value = "submodalidadUuid") String submodalidadUuid,
            @RequestBody SubmodalidadDto submodalidadDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return submodalidadService.modificarSubmodalidad(modalidadUuid, submodalidadUuid, username, submodalidadDto);
    }

    @DeleteMapping(value = SUBMODALIDAD_URI + "/{submodalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubmodalidadDto eliminarSubmodalidad(
            HttpServletRequest request,
            @PathVariable(value = "modalidadUuid") String modalidadUuid,
            @PathVariable(value = "submodalidadUuid") String submodalidadUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return submodalidadService.eliminarSubmodalidad(modalidadUuid, submodalidadUuid, username);
    }
}
