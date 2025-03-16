import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ReporteMensual from "../_models/ReporteMensual";

@Injectable({
  providedIn: 'root'
})
export class EmpresaReportesMensualesService {

  private uri: String =  environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerReportes() {
    return this.http.get(`${this.uri}/reportes-mensuales`)
  }

  obtenerReportePorUuid(uuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${uuid}`)
  }

  precargarReporte() {
    return this.http.post(`${this.uri}/reportes-mensuales/generar`, {})
  }

  guardarReporte(reporte: ReporteMensual) {
    return this.http.post(`${this.uri}/reportes-mensuales`, reporte)
  }

  obtenerMovimientosPersonalActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/personal/activos`);
  }
  obtenerMovimientosPersonalAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/personal/altas`);
  }
  obtenerMovimientosPersonalBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/personal/bajas`);
  }

  obtenerMovimientosClientesActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/clientes/activos`)
  }

  obtenerMovimientosClientesAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/clientes/altas`)
  }

  obtenerMovimientosClientesBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/clientes/bajas`)
  }

  obtenerMovimientosVehiculosActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/vehiculos/activos`)
  }

  obtenerMovimientosVehiculosAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/vehiculos/altas`)
  }

  obtenerMovimientosVehiculosBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/vehiculos/bajas`)
  }

  obtenerMovimientosArmasModalidad1Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/activas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad1Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/altas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad1Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/bajas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad2Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/activas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad2Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/altas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad2Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/bajas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad3Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/activas?modalidad=3`)
  }

  obtenerMovimientosArmasModalidad3Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/altas?modalidad=3`)
  }

  obtenerMovimientosArmasModalidad3Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/armas/bajas?modalidad=3`)
  }

  obtenerMovimientosCanesAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/canes/altas`)
  }

  obtenerMovimientosCanesBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/canes/bajas`)
  }

  obtenerMovimientosCanesActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/reportes-mensuales/${informeUuid}/canes/activos`)
  }
}
