import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class NotificacionesArgosService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerNotificacionesArgos() {
    return this.http.get(`${this.uri}/notificaciones-argos`)
  }

  leerNotificacion(notificacionUuid: string) {
    return this.http.put(`${this.uri}/notificaciones-argos/${notificacionUuid}`, {})
  }
}
