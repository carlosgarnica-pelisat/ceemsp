import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import TipoInfraestructura from "../_models/TipoInfraestructura";

@Injectable({
  providedIn: 'root'
})
export class TipoInfraestructuraService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerTiposInfraestructura() {
    return this.http.get(`${this.uri}/catalogos/tipos-infraestructura`)
  }

  obtenerTipoInfraestructuraPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/catalogos/tipos-infraestructura/${uuid}`);
  }

  guardarTipoInfraestructura(tipoInfraestructura: TipoInfraestructura) {
    return this.http.post(`${this.uri}/catalogos/tipos-infraestructura`, tipoInfraestructura);
  }

  modificarTipoInfraestructura(uuid: string, tipoInfraestructura: TipoInfraestructura) {
    return this.http.put(`${this.uri}/catalogos/tipos-infraestructura/${uuid}`, tipoInfraestructura)
  }

  eliminarTipoInfraestructura(uuid: string) {
    return this.http.delete(`${this.uri}/catalogos/tipos-infraestructura/${uuid}`)
  }
}
