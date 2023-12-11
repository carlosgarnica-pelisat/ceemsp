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
}
