import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ArmaTipo from "../_models/ArmaTipo";
import ArmaMarca from "../_models/ArmaMarca";
import ArmaClase from "../_models/ArmaClase";
import VehiculoMarca from "../_models/VehiculoMarca";
import VehiculoTipo from "../_models/VehiculoTipo";
import VehiculoUso from "../_models/VehiculoUso";
import VehiculoSubmarca from "../_models/VehiculoSubmarca";

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

  obtenerVehiculosTiposMarca(tipo: string) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/marcas/tipos/${tipo}`)
  }


  obtenerVehiculoMarcaPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/marcas/${uuid}`)
  }

  guardarVehiculoMarca(vehiculoMarca: VehiculoMarca) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/marcas`, vehiculoMarca)
  }

  modificarVehiculoMarca(uuid: string, vehiculoMarca: VehiculoMarca) {
    return this.http.put(`${this.uri}/catalogos/vehiculos/marcas/${uuid}`, vehiculoMarca)
  }

  borrarVehiculoMarcaPorUuid(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/vehiculos/marcas/${uuid}`)
  }

  guardarSubmarca(uuid: string, submarca: VehiculoSubmarca) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/marcas/${uuid}/submarcas`, submarca)
  }

  modificarSubmarca(uuid: string, submarcaUuid: string, submarca: VehiculoSubmarca) {
    return this.http.put(`${this.uri}/catalogos/vehiculos/marcas/${uuid}/submarcas/${submarcaUuid}`, submarca);
  }

  eliminarSubmarca(uuid: string, submarcaUuid: string) {
    return this.http.delete(`${this.uri}/catalogos/vehiculos/marcas/${uuid}/submarcas/${submarcaUuid}`)
  }

  // Operaciones para tipos
  obtenerVehiculosTipos() {
    return this.http.get(`${this.uri}/catalogos/vehiculos/tipos`)
  }

  obtenerVehiculoTipoPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/tipos/${uuid}`)
  }

  guardarVehiculoTipo(vehiculoTipo: VehiculoTipo) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/tipos`, vehiculoTipo)
  }

  modificarVehiculoTipo(uuid: string, vehiculoTipo: VehiculoTipo) {
    return this.http.put(`${this.uri}/catalogos/vehiculos/tipos/${uuid}`, vehiculoTipo);
  }

  borrarVehiculoTipo(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/vehiculos/tipos/${uuid}`)
  }

  // Oerapciones para usos
  obtenerVehiculosUsos() {
    return this.http.get(`${this.uri}/catalogos/vehiculos/usos`);
  }

  obtenerVehiculoUsoPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/vehiculos/usos/${uuid}`)
  };

  guardarVehiculoUso(vehiculoUso: VehiculoUso) {
    return this.http.post(`${this.uri}/catalogos/vehiculos/usos`, vehiculoUso)
  }

  modificarVehiculoUso(uuid: string, vehiculoUso: VehiculoUso) {
    return this.http.put(`${this.uri}/catalogos/vehiculos/usos/${uuid}`, vehiculoUso);
  }

  borrarVehiculoUso(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/vehiculos/usos/${uuid}`)
  }
}





