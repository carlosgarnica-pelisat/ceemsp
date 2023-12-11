import { Injectable } from '@angular/core';
import ExisteVehiculo from "../_models/ExisteVehiculo";
import ExistePersona from "../_models/ExistePersona";
import ExisteEscritura from "../_models/ExisteEscritura";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ExisteArma from "../_models/ExisteArma";

@Injectable({
  providedIn: 'root'
})
export class ValidacionService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  validarVehiculo(existeVehiculo: ExisteVehiculo) {
    return this.http.post(`${this.uri}/validaciones/vehiculos`, existeVehiculo);
  }

  validarPersona(existePersona: ExistePersona) {
    return this.http.post(`${this.uri}/validaciones/personas`, existePersona);
  }

  validarEscritura(existeEscritura: ExisteEscritura) {
    return this.http.post(`${this.uri}/validaciones/escrituras`, existeEscritura);
  }

  validarArma(existeArma: ExisteArma) {
    return this.http.post(`${this.uri}/validaciones/armas`, existeArma);
  }

  validarUsuario(something) {

  }
}
