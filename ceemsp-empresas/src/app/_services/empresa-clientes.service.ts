import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Cliente from "../_models/Cliente";
import ClienteDomicilio from "../_models/ClienteDomicilio";
import ClienteAsignacionPersonal from "../_models/ClienteAsignacionPersonal";
import ClienteModalidad from "../_models/ClienteModalidad";
import ClienteFormaEjecucion from "../_models/ClienteFormaEjecucion";

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

  obtenerClienteContrato(clienteUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/clientes/${clienteUuid}/contrato`, httpOptions)
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

  guardarDomicilioCliente(uuidCliente: string, clienteDomicilio: ClienteDomicilio) {
    return this.http.post(`${this.uri}/clientes/${uuidCliente}/domicilios`, clienteDomicilio)
  }

  modificarDomicilioCliente(uuidCliente: string, uuidDomicilio: string, clienteDomicilio: ClienteDomicilio) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`, clienteDomicilio);
  }

  cambiarDomicilioMatriz(uuid: string, uuidDomicilio: string) {
    return this.http.put(`${this.uri}/clientes/${uuid}/domicilios/${uuidDomicilio}/matriz`, {})
  }

  eliminarDomicilioCliente(uuidCliente: string, uuidDomicilio: string) {
    return this.http.delete(`${this.uri}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`)
  }

  // Cliente modalidades
  obtenerModalidadesCliente(uuidCliente: string) {
    return this.http.get(`${this.uri}/clientes/${uuidCliente}/modalidades`);
  }

  guardarModalidadCliente(uuidCliente: string, modalidadCliente: ClienteModalidad) {
    return this.http.post(`${this.uri}/clientes/${uuidCliente}/modalidades`, modalidadCliente)
  }

  modificarModalidadCliente(uuidCliente: string, uuidModalidad: string, modalidadCliente: ClienteModalidad) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}/modalidades/${uuidModalidad}`, modalidadCliente);
  }

  eliminarModalidadCliente(uuidCliente: string, uuidModalidad: string) {
    return this.http.delete(`${this.uri}/clientes/${uuidCliente}/modalidades/${uuidModalidad}`)
  }

  // Cliente asignaciones
  obtenerAsignacionesCliente(uuidCliente: string) {
    return this.http.get(`${this.uri}/clientes/${uuidCliente}/asignaciones`)
  }

  guardarAsignacionCliente(uuidCliente: string, asignacionCliente: ClienteAsignacionPersonal) {
    return this.http.post(`${this.uri}/clientes/${uuidCliente}/asignaciones`, asignacionCliente)
  }

  modificarAsignacionCliente(uuidCliente: string, uuidAsignacion: string, asignacionCliente: ClienteAsignacionPersonal) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}/asignaciones/${uuidAsignacion}`, asignacionCliente);
  }

  eliminarAsignacionCliente(uuidCliente: string, uuidAsignacion: string) {
    return this.http.delete(`${this.uri}/clientes/${uuidCliente}/asignaciones/${uuidAsignacion}`);
  }

  // Cliente forma ejecucion
  obtenerFormasEjecucionCliente(uuidCliente: string) {
    return this.http.get(`${this.uri}/clientes/${uuidCliente}/formas-ejecucion`)
  }

  guardarFormaEjecucionCliente(uuidCliente: string, formaEjecucion: ClienteFormaEjecucion) {
    return this.http.post(`${this.uri}/clientes/${uuidCliente}/formas-ejecucion`, formaEjecucion)
  }

  modificarFormaEjecucionCliente(uuidCliente: string, uuidFormaEjecucionCliente: string, formaEjecucion: ClienteFormaEjecucion) {
    return this.http.put(`${this.uri}/clientes/${uuidCliente}/formas-ejecucion/${uuidFormaEjecucionCliente}`, formaEjecucion)
  }

  eliminarFormaEjecucionCliente(uuidCliente: string, uuidFormaEjecucionCliente: string) {
    return this.http.delete(`${this.uri}/clientes/${uuidCliente}/formas-ejecucion/${uuidFormaEjecucionCliente}`)
  }
}
