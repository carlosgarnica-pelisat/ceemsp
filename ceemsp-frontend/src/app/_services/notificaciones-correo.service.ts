import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class NotificacionesCorreoService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  enviarNotificacionCorreo(tipoNotificacion: string) {
    return this.http.post(`${this.uri}/notificaciones-email`, {notificacion: tipoNotificacion})
  }
}
