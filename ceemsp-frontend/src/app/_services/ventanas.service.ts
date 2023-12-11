import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Ventana from "../_models/Ventana";

@Injectable({
  providedIn: 'root'
})
export class VentanasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerVentanas() {
    return this.http.get(`${this.uri}/ventanas`)
  }

  obtenerVentanaPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/ventanas/${uuid}`)
  }

  guardarVentana(ventana: Ventana) {
    return this.http.post(`${this.uri}/ventanas`, ventana)
  }

  modificarVentana(uuid: string, ventana: Ventana) {
    return this.http.put(`${this.uri}/ventanas/${uuid}`, ventana)
  }

  eliminarVentana(uuid: string) {
    return this.http.delete(`${this.uri}/ventanas/${uuid}`)
  }
}
