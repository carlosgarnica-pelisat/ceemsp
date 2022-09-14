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

  obtenerEquipoByUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/equipos/${uuid}`)
  }
}
