import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Equipo from "../_models/Equipo";
import Uniforme from "../_models/Uniforme";

@Injectable({
  providedIn: 'root'
})
export class UniformeService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerUniformes() {
    return this.http.get(`${this.uri}/catalogos/uniformes`)
  }

  obtenerUniformeByUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/uniformes/${uuid}`)
  }

  guardarUniforme(uniforme: Uniforme) {
    return this.http.post(`${this.uri}/catalogos/uniformes`, uniforme)
  }

  modificarUniforme(uuid: string, uniforme: Uniforme) {
    return this.http.put(`${this.uri}/catalogos/uniformes/${uuid}`, uniforme)
  }

  eliminarUniforme(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/uniformes/${uuid}`)
  }
}
