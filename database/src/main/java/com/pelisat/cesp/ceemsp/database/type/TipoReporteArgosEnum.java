package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoReporteArgosEnum {
    LISTADO_NOMINAL("LISTADO_NOMINAL", "Listado nominal (personal tecnico)", "El reporte contiene toda la informacion del personal del tipo tecnico de todas las empresas, asi como la empresa a la que pertenece"),
    PADRON_ESTATAL("PADRON_ESTATAL", "Padron estatal", "El reporte contiene todo el padron de empresas autorizadas para prestar servicios de seguridad privada a nivel federal"),
    INTERCAMBIO_INFORMACION("INTERCAMBIO_INFORMACION", "Intercambio de informacion DGSP", "El reporte contiene la informacion general del padron"),
    ACUERDOS("ACUERDOS", "Acuerdos", "El reporte contiene todos los acuerdos de todas las empresas"),
    PERSONAL("PERSONAL", "Personal", "El reporte contiene todos el personal de todas las empresas"),
    ESCRITURAS("ESCRITURAS", "Escrituras", "El reporte contiene todas las escrituras de todas las empresas"),
    CANES("CANES", "Canes", "El reporte contiene todos los canes de todas las empresas"),
    VEHICULOS("VEHICULOS", "Vehiculos", "El reporte contiene todos los vehiculos de todas las empresas"),
    CLIENTES("CLIENTES", "Clientes", "El reporte contiene todos los clientes de todas las empresas"),
    ARMAS("ARMAS", "Armas", "El reporte contiene todas las armas de todas las empresas"),
    LICENCIAS_COLECTIVAS("LICENCIAS_COLECTIVAS", "Licencias colectivas", "El reporte contiene todas las licencias colectivas de todas las empresas"),
    VISITAS("VISITAS", "Visitas", "El reporte contiene todas las licencias colectivas de todas las empresas");

    private String codigo;
    private String nombre;
    private String descripcion;

    TipoReporteArgosEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
