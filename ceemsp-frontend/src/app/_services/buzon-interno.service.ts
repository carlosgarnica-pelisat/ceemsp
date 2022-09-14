import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import BuzonSalida from "../_models/BuzonSalida";

@Injectable({
  providedIn: 'root'
})
export class BuzonInternoService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerBuzonInterno() {
    return this.http.get(`${this.uri}/buzon-salida`)
  }

  guardarMensaje(buzonSalida: BuzonSalida) {
    return this.http.post(`${this.uri}/buzon-salida`, buzonSalida);
  }

  obtenerMensajePorUuid(uuid: string) {
    return this.http.get(`${this.uri}/buzon-salida/${uuid}`);
  }

  modificarMensajePorUuid(uuid: string, buzonSalida: BuzonSalida) {
    return this.http.put(`${this.uri}/buzon-salida/${uuid}`, buzonSalida);
  }

  eliminarMensajePorUuid(uuid: string) {
    return this.http.delete(`${this.uri}/buzon-salida/${uuid}`)
  }
}

