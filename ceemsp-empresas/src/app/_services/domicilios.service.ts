import { Injectable } from '@angular/core';
import EmpresaDomicilio from "../_models/EmpresaDomicilio";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class DomiciliosService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerDomicilios(uuid: string) {
    return this.http.get(`${this.uri}/domicilios`);
  }

  obtenerDomicilioPorUuid(uuid: string, domicilioUuid: string) {
    return this.http.get(`${this.uri}/domicilios/${domicilioUuid}`)
  }

  guardarDomicilio(uuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/domicilios`, empresaDomicilio)
  }

  modificarDomicilio(uuid: string, domicilioUuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.put(`${this.uri}/domicilios/${domicilioUuid}`, empresaDomicilio);
  }

  eliminarDomicilio(uuid: string, domicilioUuid: string) {
    return this.http.delete(`${this.uri}/domicilios/${domicilioUuid}`);
  }
}
