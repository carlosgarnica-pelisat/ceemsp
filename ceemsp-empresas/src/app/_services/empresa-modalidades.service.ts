import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import EmpresaModalidad from "../_models/EmpresaModalidad";

@Injectable({
  providedIn: 'root'
})
export class EmpresaModalidadesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerModalidades() {
    return this.http.get(`${this.uri}/modalidades`)
  }
}
