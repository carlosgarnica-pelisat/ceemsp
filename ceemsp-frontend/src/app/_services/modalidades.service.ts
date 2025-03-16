import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import CanRaza from "../_models/CanRaza";
import Modalidad from "../_models/Modalidad";
import Submodalidad from "../_models/Submodalidad";

@Injectable({
  providedIn: 'root'
})
export class ModalidadesService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerModalidades() {
    return this.http.get(`${this.uri}/catalogos/modalidades`)
  }

  obtenerModalidadesPorFiltro(filtro: string, valor: string) {
    return this.http.get(`${this.uri}/catalogos/modalidades?filterBy=${filtro}&filterValue=${valor}`);
  }

  obtenerModalidadByUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/modalidades/${uuid}`)
  }

  guardarmodalidad(modalidad: Modalidad) {
    return this.http.post(`${this.uri}/catalogos/modalidades`, modalidad)
  }

  modificarModalidad(uuid: string, modalidad: Modalidad) {
    return this.http.put(`${this.uri}/catalogos/modalidades/${uuid}`, modalidad)
  }

  borrarModalidad(uuid: String) {
    return this.http.delete(`${this.uri}/catalogos/modalidades/${uuid}`)
  }

  // Submodalidades
  obtenerSubmodalidades(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/modalidades/${uuid}/submodalidades`)
  }

  guardarSubmodalidad(uuid: string, submodalidad: Submodalidad) {
    return this.http.post(`${this.uri}/catalogos/modalidades/${uuid}/submodalidades`, submodalidad)
  }

  modificarSubmodalidad(uuid: string, submodalidadUuid: string, submodalidad: Submodalidad) {
    return this.http.put(`${this.uri}/catalogos/modalidades/${uuid}/submodalidades/${submodalidadUuid}`, submodalidad)
  }

  eliminarSubmodalidad(uuid: string, submodalidadUuid: string) {
    return this.http.delete(`${this.uri}/catalogos/modalidades/${uuid}/submodalidades/${submodalidadUuid}`)
  }
}
