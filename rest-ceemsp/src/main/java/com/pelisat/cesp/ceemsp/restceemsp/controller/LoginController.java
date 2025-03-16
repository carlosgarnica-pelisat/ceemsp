package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CredentialDto;
import com.pelisat.cesp.ceemsp.database.dto.JwtDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.JwtUserDetailsService;
import com.pelisat.cesp.ceemsp.restceemsp.service.UsuarioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtToken jwtToken;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UsuarioService usuarioService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtToken jwtToken,
                           JwtUserDetailsService jwtUserDetailsService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtToken = jwtToken;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.usuarioService = usuarioService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid CredentialDto credentialDto) throws Exception {
        authenticate(credentialDto.getEmail(), credentialDto.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(credentialDto.getEmail());
        final String token = jwtToken.generateToken(userDetails);
        final UsuarioDto usuarioDto = usuarioService.getUserByEmail(credentialDto.getEmail());
        return ResponseEntity.ok(new JwtDto(token, usuarioDto));
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logoutAdmin() throws Exception {
        return ResponseEntity.ok(null);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch(DisabledException dex) {
            throw new Exception("USER_DISABLED", dex);
        } catch(BadCredentialsException bce) {
            throw new Exception("INVALID_CREDENTIALS", bce  );
        }
    }
}
