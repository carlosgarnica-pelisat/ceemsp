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

  guardarComunicado(comunicadoGeneral: ComunicadoGeneral) {
    return this.http.post(`${this.uri}/comunicados-generales`, comunicadoGeneral);
  }
}
