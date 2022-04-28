import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Uniforme from "../_models/Uniforme";
import Usuario from "../_models/Usuario";

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerUsuarios() {
    return this.http.get(`${this.uri}/usuarios`)
  }

  obtenerUsuarioByUuid(uuid: string) {
    return this.http.get(`${this.uri}/usuarios/${uuid}`)
  }

  obtenerUsuarioActual() {
    return this.http.get(`${this.uri}/usuarios/perfil`)
  }

  guardarUsuario(usuario: Usuario) {
    return this.http.post(`${this.uri}/usuarios`, usuario)
  }

  modificarUsuario(uuid: string, usuario: Usuario) {
    return this.http.put(`${this.uri}/usuarios/${uuid}`, usuario)
  }

  eliminarUsuario(uuid: string) {
    return this.http.delete(`${this.uri}/usuarios/${uuid}`)
  }
}
