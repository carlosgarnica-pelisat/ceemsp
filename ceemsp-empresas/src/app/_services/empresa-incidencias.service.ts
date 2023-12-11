import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Incidencia from "../_models/Incidencia";
import Persona from "../_models/Persona";
import Arma from "../_models/Arma";
import Vehiculo from "../_models/Vehiculo";
import Can from "../_models/Can";

@Injectable({
  providedIn: 'root'
})
export class EmpresaIncidenciasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Incidencias
  obtenerIncidenciasPorEmpresa() {
    return this.http.get(`${this.uri}/incidencias`);
  }

  obtenerIncidenciaPorUuid(incidenciaUuid: string) {
    return this.http.get(`${this.uri}/incidencias/${incidenciaUuid}`);
  }

  guardarIncidencia(incidencia: FormData) {
    return this.http.post(`${this.uri}/incidencias`, incidencia, {
      headers: {'X-isFile': 'true'}});
  }

  autoasignarIncidencia(incidenciaUuid: string) {
    return this.http.put(`${this.uri}/incidencias/${incidenciaUuid}/autoasignar`, {})
  }

  agregarComentario(incidenciaUuid: string, incidencia: FormData) {
    return this.http.post(`${this.uri}/incidencias/${incidenciaUuid}/comentarios`, incidencia, {
      headers: {'X-isFile': 'true'}});
  }

  agregarPersonaIncidencia(incidenciaUuid: string, personal: Persona) {
    return this.http.post(`${this.uri}/incidencias/${incidenciaUuid}/personal`, personal)
  }

  agregarArmaIncidencia(incidenciaUuid: string, arma: Arma) {
    return this.http.post(`${this.uri}/incidencias/${incidenciaUuid}/armas`, arma)
  }

  agregarVehiculoIncidencia(incidenciaUuid: string, vehiculo: Vehiculo) {
    return this.http.post(`${this.uri}/incidencias/${incidenciaUuid}/vehiculos`, vehiculo)
  }

  agregarCanIncidencia(incidenciaUuid: string, can: Can) {
    return this.http.post(`${this.uri}/incidencias/${incidenciaUuid}/canes`, can)
  }

  eliminarPersonaIncidencia(incidenciaUuid: string, incidenciaPersonaUuid: string) {
    return this.http.delete(`${this.uri}/incidencias/${incidenciaUuid}/personal/${incidenciaPersonaUuid}`)
  }

  eliminarArmaIncidencia(incidenciaUuid: string, incidenciaArmaUuid: string) {
    return this.http.delete(`${this.uri}/incidencias/${incidenciaUuid}/armas/${incidenciaArmaUuid}`)
  }

  eliminarVehiculoIncidencia(incidenciaUuid: string, incidenciaVehiculoUuid: string) {
    return this.http.delete(`${this.uri}/incidencias/${incidenciaUuid}/vehiculos/${incidenciaVehiculoUuid}`)
  }

  eliminarCanIncidencia(incidenciaUuid: string, incidenciaCanUuid: string) {
    return this.http.delete(`${this.uri}/incidencias/${incidenciaUuid}/canes/${incidenciaCanUuid}`)
  }

  descargarArchivoIncidencia(incidenciaUuid: string, incidenciaArchivoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/incidencias/${incidenciaUuid}/archivos/${incidenciaArchivoUuid}/descargar`, httpOptions)
  }

  agregarArchivoIncidencia(incidenciaUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/incidencias/${incidenciaUuid}/archivos`,
      formData, {headers: {'X-isFile': 'true'}})
  }
}
