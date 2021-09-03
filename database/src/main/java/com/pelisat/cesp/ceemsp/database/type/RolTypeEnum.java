package com.pelisat.cesp.ceemsp.database.type;

public enum RolTypeEnum {

    CEEMSP_SUPERUSER("CEEMSP_SUPERUSER", "Superusuario", "Puede manejar toda la plataforma"),
    CEEMSP_USER("CEEMSP_USER", "Usuario", "Usuario capaz de realizar operaciones sobre el ceemsp"),
    ENTERPRISE_USER("ENTERPRISE_USER", "Usuario", "Usuario capaz de realizar operaciones sobre la empresa");

    private String code;
    private String name;
    private String description;

    RolTypeEnum(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
