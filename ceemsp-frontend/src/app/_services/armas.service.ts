import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import CanRaza from "../_models/CanRaza";
import CanEntrenamiento from "../_models/CanEntrenamiento";
import ArmaTipo from "../_models/ArmaTipo";
import ArmaMarca from "../_models/ArmaMarca";
import ArmaClase from "../_models/ArmaClase";

@Injectable({
  providedIn: 'root'
})
export class ArmasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para tipos de arma
  obtenerArmasTipos() {
    return this.http.get(`${this.uri}/catalogos/armas/tipos`)
  }

  obtenerArmaTipoPorUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/armas/tipos/${uuid}`)
  }

  guardarArmaTipo(armaTipo: ArmaTipo) {
    return this.http.post(`${this.uri}/catalogos/armas/tipos`, armaTipo)
  }

  borrarArmaTipoPorUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/armas/tipos/${uuid}`)
  }

  // Operaciones para marcas de arma
  obtenerArmaMarcas() {
    return this.http.get(`${this.uri}/catalogos/armas/marcas`)
  }

  obtenerArmaMarcaPorUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/armas/marcas/${uuid}`)
  }

  guardarArmaMarca(armaMarca: ArmaMarca) {
    return this.http.post(`${this.uri}/catalogos/armas/marcas`, armaMarca)
  }

  borrarArmaMarcaPorUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/armas/marcas/${uuid}`)
  }

  // Operaciones para clases de arma
  obtenerArmaClases() {
    return this.http.get(`${this.uri}/catalogos/armas/clases`)
  }

  obtenerArmaClasePorUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/armas/clases/${uuid}`)
  }

  guardarArmaClase(armaClase: ArmaClase) {
    return this.http.post(`${this.uri}/catalogos/armas/clases`, armaClase)
  }

  borrarArmaClasePorUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/armas/clases/${uuid}`)
  }
}
