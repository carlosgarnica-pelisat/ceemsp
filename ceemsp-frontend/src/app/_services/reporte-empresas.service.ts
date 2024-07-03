import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ReporteEmpresasService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  generarReportePadronEmpresas(status: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/padron?status=${status}`, {}, httpOptions);
  }

  generarReporteAcuerdos(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/acuerdos`, {}, httpOptions);
  }

  generarReporteDomicilios(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/domicilios`, {}, httpOptions);
  }

  generarReportePersonal(uuid: string, eliminado: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/personal?eliminados=${eliminado}`, {}, httpOptions);
  }
  generarReporteCanes(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/canes`, {}, httpOptions);
  }

  generarReporteVehiculos(uuid: string, eliminado: boolean) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/vehiculos?eliminados=${eliminado}`, {}, httpOptions);
  }

  generarReporteClientes(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/clientes`, {}, httpOptions);
  }

  generarReporteArmas(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/armas`, {}, httpOptions);
  }

  generarReporteEscrituras(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/escrituras`, {}, httpOptions);
  }

  generarReporteLicenciasColectivas(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/licencias-colectivas`, {}, httpOptions);
  }

  generarReporteVisitas(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/visitas`, {}, httpOptions);
  }

  generarReporteEquipos(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/empresas/${uuid}/reporteo/equipos`, {}, httpOptions);
  }
}
