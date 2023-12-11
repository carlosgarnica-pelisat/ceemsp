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

  obtenerMovimientosPersonalAltas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/personal/altas`);
  }
  obtenerMovimientosPersonalBajas(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/personal/bajas`);
  }

  obtenerMovimientosClientes(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/clientes`)
  }

  obtenerMovimientosVehiculos(uuid: string, informeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/informes-mensuales/${informeUuid}/vehiculos`)
  }
}
