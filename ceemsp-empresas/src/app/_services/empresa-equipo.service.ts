import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import EmpresaUniforme from "../_models/EmpresaUniforme";
import EmpresaEquipo from "../_models/EmpresaEquipo";

@Injectable({
  providedIn: 'root'
})
export class EmpresaEquipoService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEquipos() {
    return this.http.get(`${this.uri}/equipos`);
  }

  obtenerEquipoPorUuid(uuidEquipo: string) {
    return this.http.get(`${this.uri}/equipos/${uuidEquipo}`);
  }

  guardarEquipo(equipo: EmpresaEquipo) {
    return this.http.post(`${this.uri}/equipos`, equipo);
  }

  modificarEquipo(uuidEquipo: string, equipo: EmpresaEquipo) {
    return this.http.put(`${this.uri}/equipos/${uuidEquipo}`, equipo);
  }

  eliminarEquipo(uuidEquipo: string) {
    return this.http.delete(`${this.uri}/equipos/${uuidEquipo}`);
  }
}
