import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Modalidad from "../_models/Modalidad";
import Equipo from "../_models/Equipo";

@Injectable({
  providedIn: 'root'
})
export class EquipoService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEquipos() {
    return this.http.get(`${this.uri}/catalogos/equipos`)
  }

  obtenerEquiposCalificablesParaEmpresa(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/equipos?empresaUuid=${uuid}`)
  }

  obtenerEquipoByUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/equipos/${uuid}`)
  }

  guardarEquipo(equipo: Equipo) {
    return this.http.post(`${this.uri}/catalogos/equipos`, equipo)
  }

  modificarEquipo(equipoUuid: string, equipo: Equipo) {
    return this.http.put(`${this.uri}/catalogos/equipos/${equipoUuid}`, equipo);
  }

  eliminarEquipo(equipoUuid: string) {
    return this.http.delete(`${this.uri}/catalogos/equipos/${equipoUuid}`)
  }
}
