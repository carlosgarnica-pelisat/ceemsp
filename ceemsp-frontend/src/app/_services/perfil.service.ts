import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ActualizarContrasena from "../_models/ActualizarContrasena";

@Injectable({
  providedIn: 'root'
})
export class PerfilService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  cambiarContrasnea(actualizarContrasena: ActualizarContrasena) {
    return this.http.post(`${this.uri}/perfil/contrasena`, actualizarContrasena);
  }
}
