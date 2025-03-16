import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EstadosService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEstados() {
    return this.http.get(`${this.uri}/catalogos/estados`)
  }

  obtenerEstadosPorMunicipio(uuidEstado: string) {
    return this.http.get(`${this.uri}/catalogos/estados/${uuidEstado}/municipios`);
  }

  obtenerLocalidadesPorMunicipioYEstado(uuidEstado: string, uuidMunicipio: string) {
    return this.http.get(`${this.uri}/catalogos/estados/${uuidEstado}/municipios/${uuidMunicipio}/localidades`);
  }

  obtenerColoniasPorMunicipioYEstado(uuidEstado: string, uuidMunicipio: string) {
    return this.http.get(`${this.uri}/catalogos/estados/${uuidEstado}/municipios/${uuidMunicipio}/colonias`);
  }
}
