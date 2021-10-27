import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import CanRaza from "../_models/CanRaza";
import Modalidad from "../_models/Modalidad";

@Injectable({
  providedIn: 'root'
})
export class ModalidadesService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para razas
  obtenerModalidades() {
    return this.http.get(`${this.uri}/catalogos/modalidades`)
  }

  obtenerModalidadesPorFiltro(filtro: string, valor: string) {
    return this.http.get(`${this.uri}/catalogos/modalidades?filterBy=${filtro}&filterValue=${valor}`);
  }

  obtenerModalidadByUuid(uuid: String) {
    return this.http.get(`${this.uri}/catalogos/modalidades/${uuid}`)
  }

  guardarmodalidad(modalidad: Modalidad) {
    return this.http.post(`${this.uri}/catalogos/modalidades`, modalidad)
  }

  deleteModalidadByUuid(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/modalidades/${uuid}`)
  }
}
