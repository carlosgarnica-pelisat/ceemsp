import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import EmpresaDomicilio from "../_models/EmpresaDomicilio";

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEmpresas() {
    return this.http.get(`${this.uri}/empresas`)
  }

  guardarEmpresa(empresa: Empresa) {
    return this.http.post(`${this.uri}/empresas`, empresa);
  }

  obtenerPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}`);
  }

  // Addresses
  obtenerDomicilios(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/domicilios`);
  }

  guardarDomicilio(uuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/empresas/${uuid}/domicilios`, empresaDomicilio)
  }
}
