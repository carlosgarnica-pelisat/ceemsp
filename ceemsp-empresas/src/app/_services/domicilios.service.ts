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

  obtenerDomicilios() {
    return this.http.get(`${this.uri}/domicilios`);
  }

  obtenerDomicilioPorUuid(domicilioUuid: string) {
    return this.http.get(`${this.uri}/domicilios/${domicilioUuid}`)
  }

  guardarDomicilio(empresaDomicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/domicilios`, empresaDomicilio)
  }

  modificarDomicilio(domicilioUuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.put(`${this.uri}/domicilios/${domicilioUuid}`, empresaDomicilio);
  }

  eliminarDomicilio(domicilioUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/domicilios/${domicilioUuid}/borrar`, formData, {
        headers: {'X-isFile': 'true'}
      })
  }
}
