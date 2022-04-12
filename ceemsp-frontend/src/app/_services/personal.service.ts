import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import PersonalPuestoTrabajo from "../_models/PersonalPuestoTrabajo";
import PersonalNacionalidad from "../_models/PersonalNacionalidad";
import PersonalSubpuestoTrabajo from "../_models/PersonalSubpuestoTrabajo";

@Injectable({
  providedIn: 'root'
})
export class PersonalService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para personal
  obtenerPuestosPersonal() {
    return this.http.get(`${this.uri}/catalogos/personal/puestos`);
  }

  guardarPuesto(puestoTrabajo: PersonalPuestoTrabajo) {
    return this.http.post(`${this.uri}/catalogos/personal/puestos`, puestoTrabajo);
  }

  obtenerPuestoPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/personal/puestos/${uuid}`);
  }

  obtenerSubpuestosTrabajo(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/personal/puestos/${uuid}/subpuestos`)
  }

  guardarSubpuestoTrabajo(uuid: string, subpuestoTrabajo: PersonalSubpuestoTrabajo) {
    return this.http.post(`${this.uri}/catalogos/personal/puestos/${uuid}/subpuestos`, subpuestoTrabajo);
  }

  // Operaciones para nacionalidades
  obtenerNacionalidades() {
    return this.http.get(`${this.uri}/catalogos/personal/nacionalidades`);
  }

  guardarNacionalidad(nacionalidad: PersonalNacionalidad) {
    return this.http.post(`${this.uri}/catalogos/personal/nacionalidades`, nacionalidad);
  }

  modificarNacionalidad(uuid: string, nacionalidad: PersonalNacionalidad) {
    return this.http.put(`${this.uri}/catalogos/personal/nacionalidades/${uuid}`, nacionalidad)
  }

  eliminarNacionalidad(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/personal/nacionalidades/${uuid}`);
  }
}


