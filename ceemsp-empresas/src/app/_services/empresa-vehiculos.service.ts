import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import VehiculoColor from "../_models/VehiculoColor";
import Vehiculo from "../_models/Vehiculo";

@Injectable({
  providedIn: 'root'
})
export class EmpresaVehiculosService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerVehiculos() {
    return this.http.get(`${this.uri}/vehiculos`);
  }

  obtenerVehiculoPorUuid(vehiculoUuid: string) {
    return this.http.get(`${this.uri}/vehiculos/${vehiculoUuid}`);
  }

  guardarVehiculo(vehiculo: Vehiculo) {
    return this.http.post(`${this.uri}/vehiculos`, vehiculo);
  }

  modificarVehiculo(vehiculoUuid: string, vehiculo: Vehiculo) {
    return this.http.put(`${this.uri}/vehiculos/${vehiculoUuid}`, vehiculo);
  }

  eliminarVehiculo(vehiculoUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/vehiculos/${vehiculoUuid}/borrar`, formData,{
        headers: {'X-isFile': 'true'}
      })
  }

  obtenerVehiculoColores(vehiculoUuid: string) {
    return this.http.get(`${this.uri}/vehiculos/${vehiculoUuid}/colores`)
  }

  guardarVehiculoColor(vehiculoUuid: string, color: VehiculoColor) {
    return this.http.post(`${this.uri}/vehiculos/${vehiculoUuid}/colores`, color);
  }

  modificarVehiculoColor(vehiculoUuid: string, colorUuid: string, color: VehiculoColor) {
    return this.http.put(`${this.uri}/vehiculos/${vehiculoUuid}/colores/${colorUuid}`, color);
  }

  eliminarVehiculoColor(vehiculoUuid: string, colorUuid: string) {
    return this.http.delete(`${this.uri}/vehiculos/${vehiculoUuid}/colores/${colorUuid}`);
  }

  listarVehiculoFotografias(vehiculoUuid: string) {
    return this.http.get(`${this.uri}/vehiculos/${vehiculoUuid}/fotografias`);
  }

  guardarVehiculoFotografia(vehiculoUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/vehiculos/${vehiculoUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarVehiculoFotografia(vehiculoUuid: string, fotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    }

    return this.http.get(`${this.uri}/vehiculos/${vehiculoUuid}/fotografias/${fotografiaUuid}`, httpOptions);
  }

  eliminarVehiculoFotografia(vehiculoUuid: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/vehiculos/${vehiculoUuid}/fotografias/${fotografiaUuid}`);
  }
}
