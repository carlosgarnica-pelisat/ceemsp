import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import BuzonSalida from "../_models/BuzonSalida";
import BuzonInternoDestinatario from "../_models/BuzonInternoDestinatario";

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

  // Destinatarios
  agregarDestinatario(uuid: string, destinatario: BuzonInternoDestinatario) {
    return this.http.post(`${this.uri}/buzon-salida/${uuid}/destinatarios`, destinatario)
  }

  modificarDestinatario(uuid: string, uuidDestinatario: string, destinatario: BuzonInternoDestinatario) {
    return this.http.put(`${this.uri}/buzon-salida/${uuid}/destinatarios/${uuidDestinatario}`, destinatario)
  }

  eliminarDestinatario(uuid: string, uuidDestinatario: string) {
    return this.http.delete(`${this.uri}/buzon-salida/${uuid}/destinatarios/${uuidDestinatario}`)
  }
}

