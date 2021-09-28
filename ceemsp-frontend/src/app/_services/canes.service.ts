import { Injectable } from '@angular/core';
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import CanRaza from "../_models/CanRaza";
import CanEntrenamiento from "../_models/CanEntrenamiento";

@Injectable({
  providedIn: 'root'
})
export class CanesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para razas
  getAllRazas() {
    return this.http.get(`${this.uri}/catalogos/canes/razas`)
  }

  getRazaByUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/canes/razas/${uuid}`)
  }

  saveRaza(canRaza: CanRaza) {
    return this.http.post(`${this.uri}/catalogos/canes/razas`, canRaza)
  }

  deleteRazaByUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/canes/razas/${uuid}`)
  }

  // Operaciones para entrenamientos
  getAllEntrenamientos() {
    return this.http.get(`${this.uri}/catalogos/canes/entrenamientos`)
  }

  getEntrenamientoByUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/canes/entrenamientos/${uuid}`)
  }

  saveEntrenamiento(canEntrenamiento: CanEntrenamiento) {
    return this.http.post(`${this.uri}/catalogos/canes/entrenamientos`, canEntrenamiento)
  }

  deleteEntrenamientoByUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/canes/entrenamientos/${uuid}`)
  }
}
