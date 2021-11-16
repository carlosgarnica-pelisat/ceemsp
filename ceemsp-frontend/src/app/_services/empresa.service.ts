import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import EmpresaDomicilio from "../_models/EmpresaDomicilio";
import EmpresaEscritura from "../_models/EmpresaEscritura";
import Vehiculo from "../_models/Vehiculo";
import EmpresaModalidad from "../_models/EmpresaModalidad";
import EmpresaLicenciaColectiva from "../_models/EmpresaLicenciaColectiva";
import EmpresaEscrituraSocio from "../_models/EmpresaEscrituraSocio";
import EmpresaEscrituraApoderado from "../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../_models/EmpresaEscrituraRepresentante";
import Cliente from "../_models/Cliente";

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

  // Modalidades
  obtenerModalidades(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/modalidades`)
  }

  guardarModalidad(empresaUuid: string, modalidad: EmpresaModalidad) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/modalidades`, modalidad)
  }

  // Domicilios
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

  obtenerEscrituraPorUuid(uuid: string, uuidEscritura: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/escrituras/${uuidEscritura}`);
  }

  // Escrituras socios
  obtenerEscrituraSocios(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`);
  }

  guardarEscrituraSocio(empresaUuid: string, escrituraUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`, escrituraSocio);
  }

  // Escrituras representantes
  obtenerEscrituraRepresentantes(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`);
  }

  guardarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`, escrituraRepresentante);
  }

  // Escrituras apoderados
  obtenerEscriturasApoderados(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`);
  }

  guardarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`, escrituraApoderado);
  }

  // Licencias colectivas
  obtenerLicenciasColectivas(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias`);
  }

  guardarLicenciaColectiva(uuid: string, licenciaColectiva: EmpresaLicenciaColectiva) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias`, licenciaColectiva);
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

  obtenerClientePorUuid(uuid: string, clienteUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/${clienteUuid}`)
  }

  guardarCliente(uuid: string, cliente: Cliente) {
    return this.http.post(`${this.uri}/empresas/${uuid}/clientes`, cliente)
  }
}
