import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class NotificacionesService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerNotificaciones() {
    return this.http.get(`${this.uri}/notificaciones`)
  }

  leerNotificacion(uuid: string) {
    return this.http.get(`${this.uri}/notificaciones/${uuid}`)
  }
}
