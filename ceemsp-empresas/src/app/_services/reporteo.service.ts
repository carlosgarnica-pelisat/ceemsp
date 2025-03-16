import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
@Injectable({
  providedIn: 'root'
})
export class ReporteoService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  generarReporteAcuerdos() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/acuerdos`, {}, httpOptions);
  }

  generarReportePersonal(eliminados: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/personal?eliminados=${eliminados}`, {}, httpOptions);
  }

  generarReporteCanes(eliminados: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/canes?eliminados=${eliminados}`, {}, httpOptions);
  }

  generarReporteVehiculos(eliminados: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/vehiculos?eliminados=${eliminados}`, {}, httpOptions);
  }

  generarReporteClientes(eliminados: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/clientes?eliminados=${eliminados}`, {}, httpOptions);
  }

  generarReporteArmas() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/armas`, {}, httpOptions);
  }

  generarReporteLicenciasColectivas() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/licencias-colectivas`, {}, httpOptions);
  }

  generarReporteVisitas(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/visitas`, {}, httpOptions);
  }
}
