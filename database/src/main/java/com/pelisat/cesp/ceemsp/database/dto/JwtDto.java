package com.pelisat.cesp.ceemsp.database.dto;

public class JwtDto {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwtToken;

    public JwtDto(String jwttoken) {
        this.jwtToken = jwttoken;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
