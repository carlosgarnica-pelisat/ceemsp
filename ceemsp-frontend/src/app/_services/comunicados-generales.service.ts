import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ComunicadoGeneral from "../_models/ComunicadoGeneral";

@Injectable({
  providedIn: 'root'
})
export class ComunicadosGeneralesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerComunicados() {
    return this.http.get(`${this.uri}/comunicados-generales`)
  }

  obtenerComunicadoPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/comunicados-generales/${uuid}`)
  }

  guardarComunicado(comunicadoGeneral: ComunicadoGeneral) {
    return this.http.post(`${this.uri}/comunicados-generales`, comunicadoGeneral);
  }

  modificarComunicado(uuid: string, comunicadoGeneral: ComunicadoGeneral) {
    return this.http.put(`${this.uri}/comunicados-generales/${uuid}`, comunicadoGeneral)
  }

  eliminarComunicado(uuid: string) {
    return this.http.delete(`${this.uri}/comunicados-generales/${uuid}`)
  }
}
