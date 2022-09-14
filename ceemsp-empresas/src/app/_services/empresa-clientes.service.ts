import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Cliente from "../_models/Cliente";
import ClienteDomicilio from "../_models/ClienteDomicilio";

@Injectable({
  providedIn: 'root'
})
export class EmpresaClientesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  // Clientes
  obtenerClientes() {
    return this.http.get(`${this.uri}/clientes`);
  }

  obtenerClientePorUuid(clienteUuid: string) {
    return this.http.get(`${this.uri}/clientes/${clienteUuid}`)
  }

  guardarCliente(formData: FormData) {
    return this.http.post(
      `${this.uri}/clientes`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  modificarCliente(uuidCliente: string, cliente: Cliente) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}`, cliente);
  }

  eliminarCliente(uuidCliente: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/clientes/${uuidCliente}/borrar`, formData,{
        headers: {'X-isFile': 'true'}
      })
  }

  // Clientes domicilios
  obtenerClienteDomicilios(uuidCliente: string) {
    return this.http.get(`${this.uri}/clientes/${uuidCliente}/domicilios`)
  }

  guardarDomicilioCliente(uuidCliente: string, clienteDomicilio: ClienteDomicilio[]) {
    return this.http.post(`${this.uri}/clientes/${uuidCliente}/domicilios`, clienteDomicilio)
  }

  modificarDomicilioCliente(uuidCliente: string, uuidDomicilio: string, clienteDomicilio: ClienteDomicilio) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`, clienteDomicilio);
  }

  eliminarDomicilioCliente(uuidCliente: string, uuidDomicilio: string) {
    return this.http.delete(`${this.uri}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`)
  }
}
