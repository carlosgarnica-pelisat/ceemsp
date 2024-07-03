import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerDatosDashboard() {
    return this.http.get(`${this.uri}/dashboard`)
  }

  obtenerResumenMovimientosMes(fechaInicio: string, fechaFin: string) {
    return this.http.get(`${this.uri}/dashboard/movimientos-mes/${fechaInicio}/${fechaFin}`);
  }

  obtenerMovimientosEmpresa(fechaInicio: string, fechaFin: string) {
    return this.http.get(`${this.uri}/dashboard/movimientos-mes/${fechaInicio}/${fechaFin}/empresas`);
  }

  obtenerEmpresasConInformesMensuales(fechaInicio: string, fechaFin: string) {
    return this.http.get(`${this.uri}/dashboard/con-movimientos/${fechaInicio}/${fechaFin}/empresas`)
  }

  obtenerEmpresasSinInformesMensuales(fechaInicio: string, fechaFin: string) {
    return this.http.get(`${this.uri}/dashboard/sin-movimientos/${fechaInicio}/${fechaFin}/empresas`)
  }
}
