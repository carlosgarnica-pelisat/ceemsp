package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PublicController {
    private EmpresaService empresaService;

    @Autowired
    public PublicController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    /*@PostMapping(value = "/public/register/next")
    public */
}
