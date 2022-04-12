import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ReporteoService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  generarReporteListadoNominal() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/listado-nominal`, {}, httpOptions);
  }

  generarReportePadronEmpresas() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.post(`${this.uri}/reporteo/padron`, {}, httpOptions);
  }
}
