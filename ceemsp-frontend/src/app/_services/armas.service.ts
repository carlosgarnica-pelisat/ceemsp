import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ArmaTipo from "../_models/ArmaTipo";
import ArmaMarca from "../_models/ArmaMarca";
import ArmaClase from "../_models/ArmaClase";

@Injectable({
  providedIn: 'root'
})
export class ArmasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para marcas de arma
  obtenerArmaMarcas() {
    return this.http.get(`${this.uri}/catalogos/armas/marcas`)
  }

  guardarArmaMarca(armaMarca: ArmaMarca) {
    return this.http.post(`${this.uri}/catalogos/armas/marcas`, armaMarca)
  }

  modificarArmaMarca(uuid: string, armaMarca: ArmaMarca) {
    return this.http.put(`${this.uri}/catalogos/armas/marcas/${uuid}`, armaMarca)
  }

  borrarArmaMarca(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/armas/marcas/${uuid}`)
  }

  // Operaciones para clases de arma
  obtenerArmaClases() {
    return this.http.get(`${this.uri}/catalogos/armas/clases`)
  }

  guardarArmaClase(armaClase: ArmaClase) {
    return this.http.post(`${this.uri}/catalogos/armas/clases`, armaClase)
  }

  modificarArmaClase(uuid: string, armaClase: ArmaClase) {
    return this.http.put(`${this.uri}/catalogos/armas/clases/${uuid}`, armaClase)
  }

  borrarArmaClase(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/armas/clases/${uuid}`)
  }
}
