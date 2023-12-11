package com.pelisat.cesp.ceemsp.database.type;

public enum VehiculoStatusEnum {
    ACTIVO("ACTIVO", "Activo", "El can se encuentra activo y en ejercicio"),
    BAJA("BAJA", "Baja", "El can se encuentra dado de baja o inactivo"),
    INSTALACIONES("INSTALACIONES", "En instalaciones", "El can se encuentra seguro en instalaciones");

    private String codigo;
    private String nombre;
    private String descripcion;

    VehiculoStatusEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
