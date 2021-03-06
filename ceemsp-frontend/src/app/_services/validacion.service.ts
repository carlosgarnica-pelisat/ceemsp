import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ExisteEmpresa from "../_models/ExisteEmpresa";
import ExisteVehiculo from "../_models/ExisteVehiculo";
import ExistePersona from "../_models/ExistePersona";
import ExisteEscritura from "../_models/ExisteEscritura";

@Injectable({
  providedIn: 'root'
})
export class ValidacionService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  validarEmpresa(existeEmpresa: ExisteEmpresa) {
    return this.http.post(`${this.uri}/validaciones/empresas`, existeEmpresa)
  }

  validarVehiculo(existeVehiculo: ExisteVehiculo) {
    return this.http.post(`${this.uri}/validaciones/vehiculos`, existeVehiculo);
  }

  validarPersona(existePersona: ExistePersona) {
    return this.http.post(`${this.uri}/validaciones/personas`, existePersona);
  }

  validarEscritura(existeEscritura: ExisteEscritura) {
    return this.http.post(`${this.uri}/validaciones/escrituras`, existeEscritura);
  }
}
