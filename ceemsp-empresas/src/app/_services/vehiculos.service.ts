import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ArmaTipo from "../_models/ArmaTipo";
import ArmaMarca from "../_models/ArmaMarca";
import ArmaClase from "../_models/ArmaClase";
import VehiculoMarca from "../_models/VehiculoMarca";
import VehiculoTipo from "../_models/VehiculoTipo";
import VehiculoUso from "../_models/VehiculoUso";

@Injectable({
  providedIn: 'root'
})
export class VehiculosService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para marcas
  obtenerVehiculosMarcas() {
    return this.http.get(`${this.uri}/catalogos/vehiculos/marcas`)
  }

  obtenerVehiculoMarcaPorUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/marcas/${uuid}`)
  }

  obtenerVehiculosTiposMarca(tipo: string) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/marcas/tipos/${tipo}`)
  }

  // Operaciones para tipos
  obtenerVehiculosTipos() {
    return this.http.get(`${this.uri}/catalogos/vehiculos/tipos`)
  }

  guardarVehiculoTipo(vehiculoTipo: VehiculoTipo) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/tipos`, vehiculoTipo)
  }

  // Oerapciones para usos
  obtenerVehiculosUsos() {
    return this.http.get(`${this.uri}/catalogos/vehiculos/usos`);
  }

  obtenerVehiculoUsoPorUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/usos/${uuid}`)
  };

  guardarVehiculoUso(vehiculoUso: VehiculoUso) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/usos`, vehiculoUso)
  }
}





