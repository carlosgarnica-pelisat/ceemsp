import { Injectable } from '@angular/core';

import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import RealizarBusqueda from "../_models/RealizarBusqueda";
import ResultadosBusqueda from "../_models/ResultadosBusqueda";
@Injectable({
  providedIn: 'root'
})
export class BusquedaService {
  private uri: String = environment.apiUrl + environment.apiVersion;
  private resultadosBusqueda: ResultadosBusqueda;
  constructor(private http: HttpClient) { }

  realizarBusqueda(realizarBusqueda: RealizarBusqueda) {
    return this.http.post(`${this.uri}/busqueda`, realizarBusqueda);
  }

  guardarResultadosBusqueda(busquedaResultado: ResultadosBusqueda) {
    this.resultadosBusqueda = busquedaResultado;
  }

  obtenerResultadosBusqueda() {
    return this.resultadosBusqueda;
  }
}
