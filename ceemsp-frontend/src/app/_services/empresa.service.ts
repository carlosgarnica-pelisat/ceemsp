import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import EmpresaDomicilio from "../_models/EmpresaDomicilio";
import EmpresaEscritura from "../_models/EmpresaEscritura";
import Vehiculo from "../_models/Vehiculo";

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

  // Escrituras
  obtenerEscrituras(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/escrituras`);
  }

  guardarEscritura(uuid: string, escritura: EmpresaEscritura) {
    return this.http.post(`${this.uri}/empresas/${uuid}/escrituras`, escritura);
  }

  // Vehiculos
  obtenerVehiculos(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos`);
  }

  guardarVehiculo(uuid: string, vehiculo: Vehiculo) {
    return this.http.post(`${this.uri}/empresas/${uuid}/vehiculos`, vehiculo);
  }

  // Clientes
  obtenerClientes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes`);
  }

  guardarCliente(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes`)
  }
}
