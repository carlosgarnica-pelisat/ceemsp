import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class NotificacionesEmpresaService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerNotificacionesArgos() {
    return this.http.get(`${this.uri}/notificaciones-empresas`)
  }

  leerNotificacion(notificacionUuid: string) {
    return this.http.put(`${this.uri}/notificaciones-empresas/${notificacionUuid}`, {})
  }
}
