import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import ExisteEmpresa from "../_models/ExisteEmpresa";

@Injectable({
  providedIn: 'root'
})
export class ValidacionService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  validarEmpresa(existeEmpresa: ExisteEmpresa) {
    return this.http.post(`${this.uri}/validaciones/empresas`, existeEmpresa)
  }
}
