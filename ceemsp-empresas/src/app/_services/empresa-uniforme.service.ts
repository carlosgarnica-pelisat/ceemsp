import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import EmpresaUniformeElemento from "../_models/EmpresaUniformeElemento";
import EmpresaUniforme from "../_models/EmpresaUniforme";

@Injectable({
  providedIn: 'root'
})
export class EmpresaUniformeService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerUniformes() {
    return this.http.get(`${this.uri}/uniformes`);
  }

  obtenerUniformePorUuid(uniformeUuid: string) {
    return this.http.get(`${this.uri}/uniformes/${uniformeUuid}`);
  }

  guardarUniforme(uniforme: EmpresaUniforme) {
    return this.http.post(`${this.uri}/uniformes`, uniforme);
  }

  modificarUniforme(uuidUniforme: string, uniforme: EmpresaUniforme) {
    return this.http.put(`${this.uri}/uniformes/${uuidUniforme}`, uniforme);
  }

  eliminarUniforme(uuidUniforme: string) {
    return this.http.delete(`${this.uri}/uniformes/${uuidUniforme}`);
  }

  guardarUniformeElemento(uniformeUuid: string, elementoUniforme: EmpresaUniformeElemento) {
    return this.http.post(`${this.uri}/uniformes/${uniformeUuid}/elementos`, elementoUniforme);
  }

  modificarUniformeElemento(uniformeUuid: string, elementoUuid: string, elementoUniforme: EmpresaUniformeElemento) {
    return this.http.put(`${this.uri}/uniformes/${uniformeUuid}/elementos/${elementoUuid}`, elementoUniforme)
  }

  eliminarUniformeElemento(uniformeUuid: string, elementoUuid: string) {
    return this.http.delete(`${this.uri}/uniformes/${uniformeUuid}/elementos/${elementoUuid}`)
  }
}
