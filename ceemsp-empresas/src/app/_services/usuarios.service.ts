import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ActualizarContrasena from "../_models/ActualizarContrasena";

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerUsuarioActual() {
    return this.http.get(`${this.uri}/usuarios/perfil`)
  }

  obtenerLogoEmpresaActual() {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/usuarios/logo`, httpOptions)
  }

  actualizarContrasena(actualizarContrasena: ActualizarContrasena) {
    return this.http.post(`${this.uri}/usuarios/cambio-contrasena`, actualizarContrasena)
  }
}
