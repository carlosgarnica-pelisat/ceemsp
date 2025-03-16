import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EmpresaVisitasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerVisitas() {
    return this.http.get(`${this.uri}/visitas`);
  }

  obtenerVisitaEmpresaPorUuid(visitaUuid: string) {
    return this.http.get(`${this.uri}/visitas/${visitaUuid}`)
  }

  obtenerVisitaArchivoPorUuid(visitaUuid: string, archivoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/visitas/${visitaUuid}/archivos/${archivoUuid}`, httpOptions);
  }
}
