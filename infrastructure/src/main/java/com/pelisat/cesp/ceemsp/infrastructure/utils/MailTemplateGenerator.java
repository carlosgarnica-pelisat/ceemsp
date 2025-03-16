package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class MailTemplateGenerator<T extends CommonModel> {
    public Map<String, String> generarPorPlantilla(NotificacionEmailEnum notificacionEmailEnum, Map<String, Object> map) {
        switch(notificacionEmailEnum) {
            case EMPRESA_REGISTRADA:
                Empresa empresaDto = (Empresa) map.get("empresa");
                Notificacion notificacion = (Notificacion) map.get("notificacion");
                Usuario usuario = (Usuario) map.get("usuario");
                Map<String, String> result = new HashMap<>();
                result.put("nombrePrestadorServicio", empresaDto.getRazonSocial());
                result.put("numeroExpediente", empresaDto.getRegistro());
                result.put("fechaHoraGeneracion", empresaDto.getFechaCreacion().toString());
                result.put("numeroNotificacion", notificacion.getNumero());
                result.put("cadenaOriginal", notificacion.getCadenaOriginal());
                result.put("sello", notificacion.getSello());
                result.put("usuario", usuario.getEmail());
                result.put("password", usuario.getPassword());
                return result;
            case NUEVA_INCIDENCIA:
                Empresa empresaDtoNuevaIncidencia = (Empresa) map.get("empresa");
                Notificacion notificacionNuevaIncidencia = (Notificacion) map.get("notificacion");
                Map<String, String> resultNuevaIncidencia = new HashMap<>();
                resultNuevaIncidencia.put("nombrePrestadorServicio", empresaDtoNuevaIncidencia.getRazonSocial());
                resultNuevaIncidencia.put("numeroExpediente", empresaDtoNuevaIncidencia.getRegistro());
                resultNuevaIncidencia.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultNuevaIncidencia.put("correo", empresaDtoNuevaIncidencia.getCorreoElectronico());
                resultNuevaIncidencia.put("numeroNotificacion", notificacionNuevaIncidencia.getNumero());
                resultNuevaIncidencia.put("cadenaOriginal", notificacionNuevaIncidencia.getCadenaOriginal());
                resultNuevaIncidencia.put("sello", notificacionNuevaIncidencia.getSello());
                return resultNuevaIncidencia;
            case INCIDENCIA_ACCION_PENDIENTE:
                Empresa empresaDtoIncidenciaPendiente = (Empresa) map.get("empresa");
                Notificacion notificacionIncidenciaPendiente = (Notificacion) map.get("notificacion");
                Map<String, String> resultAccionPendiente = new HashMap<>();
                resultAccionPendiente.put("nombrePrestadorServicio", empresaDtoIncidenciaPendiente.getRazonSocial());
                resultAccionPendiente.put("numeroExpediente", empresaDtoIncidenciaPendiente.getRegistro());
                resultAccionPendiente.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultAccionPendiente.put("correo", empresaDtoIncidenciaPendiente.getCorreoElectronico());
                resultAccionPendiente.put("numeroNotificacion", notificacionIncidenciaPendiente.getNumero());
                resultAccionPendiente.put("cadenaOriginal", notificacionIncidenciaPendiente.getCadenaOriginal());
                resultAccionPendiente.put("sello", notificacionIncidenciaPendiente.getSello());
                return resultAccionPendiente;
            case INCIDENCIA_PROCEDENTE:
                Empresa empresaDtoIncidenciaProcedente = (Empresa) map.get("empresa");
                Notificacion notificacionIncidenciaProcedente = (Notificacion) map.get("notificacion");
                Map<String, String> resultProcedente = new HashMap<>();
                resultProcedente.put("nombrePrestadorServicio", empresaDtoIncidenciaProcedente.getRazonSocial());
                resultProcedente.put("numeroExpediente", empresaDtoIncidenciaProcedente.getRegistro());
                resultProcedente.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultProcedente.put("correo", empresaDtoIncidenciaProcedente.getCorreoElectronico());
                resultProcedente.put("numeroNotificacion", notificacionIncidenciaProcedente.getNumero());
                resultProcedente.put("cadenaOriginal", notificacionIncidenciaProcedente.getCadenaOriginal());
                resultProcedente.put("sello", notificacionIncidenciaProcedente.getSello());
                return resultProcedente;
            case INCIDENCIA_IMPROCEDENTE:
                Empresa empresaDtoIncidenciaImprocedente = (Empresa) map.get("empresa");
                Notificacion notificacionIncidenciaImprocedente = (Notificacion) map.get("notificacion");
                Map<String, String> resultImprocedente = new HashMap<>();
                resultImprocedente.put("nombrePrestadorServicio", empresaDtoIncidenciaImprocedente.getRazonSocial());
                resultImprocedente.put("numeroExpediente", empresaDtoIncidenciaImprocedente.getRegistro());
                resultImprocedente.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultImprocedente.put("correo", empresaDtoIncidenciaImprocedente.getCorreoElectronico());
                resultImprocedente.put("numeroNotificacion", notificacionIncidenciaImprocedente.getNumero());
                resultImprocedente.put("cadenaOriginal", notificacionIncidenciaImprocedente.getCadenaOriginal());
                resultImprocedente.put("sello", notificacionIncidenciaImprocedente.getSello());
                return resultImprocedente;
            case NOTIFICACION:
                Empresa empresaNotificacion = (Empresa) map.get("empresa");
                Notificacion notificacionNotificacion = (Notificacion) map.get("notificacion");
                Map<String, String> resultNotificacion = new HashMap<>();
                resultNotificacion.put("nombrePrestadorServicio", empresaNotificacion.getRazonSocial());
                resultNotificacion.put("numeroExpediente", empresaNotificacion.getRegistro());
                resultNotificacion.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultNotificacion.put("correo", empresaNotificacion.getCorreoElectronico());
                resultNotificacion.put("numeroNotificacion", notificacionNotificacion.getNumero());
                resultNotificacion.put("cadenaOriginal", notificacionNotificacion.getCadenaOriginal());
                resultNotificacion.put("sello", notificacionNotificacion.getSello());
                return resultNotificacion;
            case ACUSE_RECIBO_INCIDENCIA:
                Empresa empresaAcuse = (Empresa) map.get("empresa");
                Acuse acuse = (Acuse) map.get("acuse");
                String imagen = (String) map.get("qr");
                Map<String, String> resultAcuse = new HashMap<>();
                resultAcuse.put("nombrePrestadorServicio", empresaAcuse.getRazonSocial());
                resultAcuse.put("numeroExpediente", empresaAcuse.getRegistro());
                resultAcuse.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultAcuse.put("correo", empresaAcuse.getCorreoElectronico());
                resultAcuse.put("numeroAcuseRecibo", acuse.getNumero());
                resultAcuse.put("cadenaOriginal", acuse.getCadenaOriginal());
                resultAcuse.put("sello", acuse.getSello());
                resultAcuse.put("imagenBase64", imagen);
                return resultAcuse;
            case ACUSE_INFORME_MENSUAL:
                EmpresaDto empresaInforme = (EmpresaDto) map.get("empresa");
                EmpresaReporteMensual informe = (EmpresaReporteMensual) map.get("reporte");
                String qrInforme = (String) map.get("qr");
                LocalDateTime reporteDate = informe.getFechaCreacion().minusMonths(1);
                Month mes = reporteDate.getMonth();
                Map<String, String> resultInforme = new HashMap<>();
                resultInforme.put("numeroAcuse", informe.getNumero());
                resultInforme.put("mesAnio", mes.getDisplayName(TextStyle.FULL, new Locale("es", "MX")).toUpperCase() + " " + reporteDate.getYear());
                resultInforme.put("tieneCanes", Boolean.toString(empresaInforme.isTieneCanes()));
                resultInforme.put("tieneArmas", Boolean.toString(empresaInforme.isTieneArmas()));
                resultInforme.put("nombrePrestadorServicio", empresaInforme.getRazonSocial());
                resultInforme.put("numeroExpediente", empresaInforme.getRegistro());
                resultInforme.put("fechaHoraGeneracion", informe.getFechaCreacion().toString());
                resultInforme.put("correo", empresaInforme.getCorreoElectronico());
                resultInforme.put("cadenaOriginal", informe.getCadenaOriginal());
                resultInforme.put("sello", informe.getSello());
                resultInforme.put("imagenBase64", qrInforme);
                resultInforme.put("personalActivos", Integer.toString(informe.getPersonalActivos()));
                resultInforme.put("clientesActivos", Integer.toString(informe.getClientesActivos()));
                resultInforme.put("vehiculosActivos", Integer.toString(informe.getVehiculosActivos()));
                resultInforme.put("equipoActivos", Integer.toString(informe.getEquipoActivos()));

                resultInforme.put("personalAltas", Integer.toString(informe.getPersonalAltas()));
                resultInforme.put("clientesAltas", Integer.toString(informe.getClientesAltas()));
                resultInforme.put("vehiculosAltas", Integer.toString(informe.getVehiculosAltas()));
                resultInforme.put("equipoAltas", Integer.toString(informe.getEquipoAltas()));

                resultInforme.put("personalBajas", Integer.toString(informe.getPersonalBajas()));
                resultInforme.put("clientesBajas", Integer.toString(informe.getClientesBajas()));
                resultInforme.put("vehiculosBajas", Integer.toString(informe.getVehiculosBajas()));
                resultInforme.put("equipoBajas", Integer.toString(informe.getEquipoBajas()));

                resultInforme.put("personalTotal", Integer.toString(informe.getPersonalTotal()));
                resultInforme.put("clientesTotal", Integer.toString(informe.getClientesTotal()));
                resultInforme.put("vehiculosTotal", Integer.toString(informe.getVehiculosTotal()));
                resultInforme.put("equipoTotal", Integer.toString(informe.getEquipoTotal()));

                resultInforme.put("incidenciasReportadas", Integer.toString(informe.getIncidenciasReportadas()));
                resultInforme.put("incidenciasProcedentes", Integer.toString(informe.getIncidenciasProcedentes()));
                resultInforme.put("incidenciasImprocedentes", Integer.toString(informe.getIncidenciasImprocedentes()));
                resultInforme.put("incidenciasTotal", Integer.toString(informe.getIncidenciasTotal()));

                resultInforme.put("armas1Activas", (informe.getArmas1Activas() != null) ? Integer.toString(informe.getArmas1Activas()) : "NA");
                resultInforme.put("armas2Activas", (informe.getArmas2Activas() != null) ? Integer.toString(informe.getArmas2Activas()) : "NA");
                resultInforme.put("armas3Activas", (informe.getArmas3Activas() != null) ? Integer.toString(informe.getArmas3Activas()) : "NA");

                resultInforme.put("armas1Altas", (informe.getArmas1Altas() != null) ? Integer.toString(informe.getArmas1Altas()) : "NA");
                resultInforme.put("armas2Altas", (informe.getArmas2Altas() != null) ? Integer.toString(informe.getArmas2Altas()) : "NA");
                resultInforme.put("armas3Altas", (informe.getArmas3Altas() != null) ? Integer.toString(informe.getArmas3Altas()) : "NA");

                resultInforme.put("armas1Bajas", (informe.getArmas1Bajas() != null) ? Integer.toString(informe.getArmas1Bajas()) : "NA");
                resultInforme.put("armas2Bajas", (informe.getArmas2Bajas() != null) ? Integer.toString(informe.getArmas2Bajas()) : "NA");
                resultInforme.put("armas3Bajas", (informe.getArmas3Bajas() != null) ? Integer.toString(informe.getArmas3Bajas()) : "NA");

                resultInforme.put("armas1Total", (informe.getArmas1Total() != null) ? Integer.toString(informe.getArmas1Total()) : "NA");
                resultInforme.put("armas2Total", (informe.getArmas2Total() != null) ? Integer.toString(informe.getArmas2Total()) : "NA");
                resultInforme.put("armas3Total", (informe.getArmas3Total() != null) ? Integer.toString(informe.getArmas3Total()) : "NA");

                resultInforme.put("canesAsignados", (informe.getCanesAsignados() != null) ? Integer.toString(informe.getCanesAsignados()) : "NA");
                resultInforme.put("canesInstalaciones", (informe.getCanesInstalaciones() != null) ? Integer.toString(informe.getCanesInstalaciones()) : "NA");
                resultInforme.put("canesAltas", (informe.getCanesAltas() != null) ? Integer.toString(informe.getCanesAltas()) : "NA");
                resultInforme.put("canesBajas", (informe.getCanesBajas() != null) ? Integer.toString(informe.getCanesBajas()) : "NA");
                resultInforme.put("canesTotal", (informe.getCanesTotal() != null) ? Integer.toString(informe.getCanesTotal()) : "NA");
                return resultInforme;
            case INCIDENCIA_RESPONDIDA:
                Empresa empresaDtoIncidenciaRespondida = (Empresa) map.get("empresa");
                Notificacion notificacionIncidenciaRespondida = (Notificacion) map.get("notificacion");
                Map<String, String> resultRespondida = new HashMap<>();
                resultRespondida.put("nombrePrestadorServicio", empresaDtoIncidenciaRespondida.getRazonSocial());
                resultRespondida.put("numeroExpediente", empresaDtoIncidenciaRespondida.getRegistro());
                resultRespondida.put("fechaHoraGeneracion", LocalDateTime.now().toString());
                resultRespondida.put("correo", empresaDtoIncidenciaRespondida.getCorreoElectronico());
                resultRespondida.put("numeroNotificacion", notificacionIncidenciaRespondida.getNumero());
                resultRespondida.put("cadenaOriginal", notificacionIncidenciaRespondida.getCadenaOriginal());
                resultRespondida.put("sello", notificacionIncidenciaRespondida.getSello());
                return resultRespondida;
         }

        return null;
    }
}
