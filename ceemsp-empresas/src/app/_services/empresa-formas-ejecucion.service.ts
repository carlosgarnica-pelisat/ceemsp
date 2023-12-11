import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EmpresaFormasEjecucionService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerFormasEjecucionEmpresa() {
    return this.http.get(`${this.uri}/formas-ejecucion`)
  }
}
