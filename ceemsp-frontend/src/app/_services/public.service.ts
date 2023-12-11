import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ProximaVisita from "../_models/ProximaVisita";

@Injectable({
  providedIn: 'root'
})
export class PublicService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerSiguienteNumero(body) {
    return this.http.post(`${this.uri}/public/register/next`, body)
  }

  obtenerSiguienteVisita(proximaVisita: ProximaVisita) {
    return this.http.post(`${this.uri}/public/visitas/siguiente`, proximaVisita)
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

  buscarComunicados(titulo, mes, ano) {
    return this.http.get(`${this.uri}/public/comunicados?titulo=${titulo}&mes=${mes}&ano=${ano}`);
  }

  validarAcuseSello(acuseSello: string) {
    return this.http.get(`${this.uri}/public/validar/acuse/${acuseSello}`);
  }

  validarAcuseInformeMensual(informeSello: string) {
    return this.http.get(`${this.uri}/public/validar/informe/${informeSello}`);
  }
}
