import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class PublicService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Operaciones para razas
  obtenerSiguienteNumero(body) {
    return this.http.post(`${this.uri}/public/register/next`, body)
  }

  obtenerSiguienteVisita() {
    return this.http.post(`${this.uri}/public/visitas/siguiente`, {})
  }

  obtenerUltimoComunicado() {
    return this.http.get(`${this.uri}/public/comunicados/ultimo`)
  }

  obtenerComunicados() {
    return this.http.get(`${this.uri}/public/comunicados`)
  }

  obtenerComunicadoPorUuid(uuid) {
    return this.http.get(`${this.uri}/public/comunicados/${uuid}`);
  }
}
