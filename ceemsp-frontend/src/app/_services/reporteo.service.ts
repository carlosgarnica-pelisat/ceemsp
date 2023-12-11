import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ReporteArgos from "../_models/ReporteArgos";

@Injectable({
  providedIn: 'root'
})
export class ReporteoService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerReportesArgos() {
    return this.http.get(`${this.uri}/reportes`)
  }

  programarReporteArgos(reporteArgos: ReporteArgos) {
    return this.http.post(`${this.uri}/reportes`, reporteArgos)
  }

  obtenerReporteArgosPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/reportes/${uuid}`)
  }

  descargarReporteArgosPorUuid(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };

    return this.http.get(`${this.uri}/reportes/${uuid}/archivo`, httpOptions)
  }
}
