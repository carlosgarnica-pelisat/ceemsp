import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Visita from "../_models/Visita";

@Injectable({
  providedIn: 'root'
})
export class VisitaService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerVisitas() {
    return this.http.get(`${this.uri}/visitas`)
  }

  obtenerVisitasProximas() {
    return this.http.get(`${this.uri}/visitas/proximas`)
  }

  obtenerVisitaPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/visitas/${uuid}`)
  }

  guardarVisita(visita: Visita) {
    return this.http.post(`${this.uri}/visitas`, visita);
  }

  modificarVisita(uuid: string, visita: Visita) {
    return this.http.put(`${this.uri}/visitas/${uuid}`, visita);
  }

  modificarVisitaRequerimiento(uuid: string, visita: Visita) {
    return this.http.put(`${this.uri}/visitas/${uuid}/requerimientos`, visita)
  }

  eliminarVisita(uuid: string) {
    return this.http.delete(`${this.uri}/visitas/${uuid}`);
  }

  // Archivos
  obtenerArchivosVisita(uuid: string) {
    return this.http.get(`${this.uri}/visitas/${uuid}/archivos`);
  }

  descargarArchivoVisita(uuid: string, archivoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    }

    return this.http.get(`${this.uri}/visitas/${uuid}/archivos/${archivoUuid}`, httpOptions);
  }

  guardarArchivoVisita(uuid: string, archivo: FormData) {
    return this.http.post(
      `${this.uri}/visitas/${uuid}/archivos`,
      archivo, {headers: {'X-isFile': 'true'}})
  }

  eliminarArchivoVisita(uuid: string, archivoUuid: string) {
    return this.http.delete(`${this.uri}/visitas/${uuid}/archivos/${archivoUuid}`)
  }
}
