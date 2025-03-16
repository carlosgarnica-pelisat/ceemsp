import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EmpresaInformesMensualesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerInformesMensualesPorEmpresa(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales`)
  }

  obtenerInformeMensualPorUuid(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}`)
  }

  descargarInformeExcel(uuid: string, informeUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };

    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/xls`, httpOptions)
  }

  obtenerMovimientosPersonalActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/personal/activos`);
  }
  obtenerMovimientosPersonalAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/personal/altas`);
  }
  obtenerMovimientosPersonalBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/personal/bajas`);
  }

  obtenerMovimientosClientesActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/clientes/activos`)
  }

  obtenerMovimientosClientesAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/clientes/altas`)
  }

  obtenerMovimientosClientesBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/clientes/bajas`)
  }

  obtenerMovimientosVehiculosActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/vehiculos/activos`)
  }

  obtenerMovimientosVehiculosAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/vehiculos/altas`)
  }

  obtenerMovimientosVehiculosBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/vehiculos/bajas`)
  }

  obtenerMovimientosArmasModalidad1Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/activas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad1Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/altas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad1Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/bajas?modalidad=1`)
  }

  obtenerMovimientosArmasModalidad2Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/activas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad2Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/altas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad2Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/bajas?modalidad=2`)
  }

  obtenerMovimientosArmasModalidad3Activas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/activas?modalidad=3`)
  }

  obtenerMovimientosArmasModalidad3Altas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/altas?modalidad=3`)
  }

  obtenerMovimientosArmasModalidad3Bajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/armas/bajas?modalidad=3`)
  }

  obtenerMovimientosCanesAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/canes/altas`)
  }

  obtenerMovimientosCanesBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/canes/bajas`)
  }

  obtenerMovimientosCanesActivos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/canes/activos`)
  }
}
