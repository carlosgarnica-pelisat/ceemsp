import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EmpresaAcuerdosService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Incidencias
  obtenerAcuerdosPorEmpresa() {
    return this.http.get(`${this.uri}/acuerdos`);
  }

  obtenerAcuerdoPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/acuerdos/${uuid}`);
  }

  descargarArchivoAcuerdo(uuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/acuerdos/${uuid}/archivo`, httpOptions);
  }
}
