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

  generarReportePersonal() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/personal`, {}, httpOptions);
  }
  generarReporteCanes() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/canes`, {}, httpOptions);
  }

  generarReporteVehiculos() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/vehiculos`, {}, httpOptions);
  }

  generarReporteClientes() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/clientes`, {}, httpOptions);
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
