import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import EmpresaLicenciaColectiva from "../_models/EmpresaLicenciaColectiva";
import EmpresaDomicilio from "../_models/EmpresaDomicilio";
import Arma from "../_models/Arma";

@Injectable({
  providedIn: 'root'
})
export class EmpresaLicenciasService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerLicenciasColectivas() {
    return this.http.get(`${this.uri}/licencias`);
  }

  obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/licencias/${licenciaColectivaUuid}`)
  }

  descargarLicenciaPdf(licenciaColectivaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/licencias/${licenciaColectivaUuid}/pdf`, httpOptions)
  }

  guardarLicenciaColectiva(formData: FormData) {
    return this.http.post(
      `${this.uri}/licencias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  modificarLicenciaColectiva(licenciaColectivaUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/licencias/${licenciaColectivaUuid}`, formData, {headers: {'X-isFile': 'true'}})
  }

  eliminarLicenciaColectiva(licenciaColectivaUuid: string) {
    return this.http.delete(`${this.uri}/licencias/${licenciaColectivaUuid}`)
  }


  obtenerDomiciliosPorLicenciaColectiva(licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/licencias/${licenciaColectivaUuid}/domicilios`);
  }

  guardarDomicilioEnLicenciaColectiva(licenciaColectivaUuid: string, domicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/licencias/${licenciaColectivaUuid}/domicilios`, domicilio);
  }

  eliminarDomicilioEnLicenciaColectiva(licenciaColectivaUuid: string, domicilioUuid: string) {
    return this.http.delete(`${this.uri}/licencias/${licenciaColectivaUuid}/domicilios/${domicilioUuid}`)
  }

  obtenerArmasPorLicenciaColectivaUuid(licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/licencias/${licenciaColectivaUuid}/armas`);
  }

  custodiaArma(licenciaColectivaUuid: string, armaUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}/custodia`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  modificarArma(licenciaColectivaUuid: string, armaUuid: string, arma: Arma) {
    return this.http.put(`${this.uri}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}`, arma)
  }

  guardarArma(licenciaColectivaUuid: string, arma: Arma) {
    return this.http.post(`${this.uri}/licencias/${licenciaColectivaUuid}/armas`, arma);
  }

  eliminarArma(licenciaColectivaUuid: string, armaUuid: string) {
    return this.http.delete(`${this.uri}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}`);
  }
}
