import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import EmpresaEscritura from "../_models/EmpresaEscritura";
import EmpresaEscrituraSocio from "../_models/EmpresaEscrituraSocio";
import EmpresaEscrituraRepresentante from "../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../_models/EmpresaEscrituraConsejo";
import EmpresaEscrituraApoderado from "../_models/EmpresaEscrituraApoderado";

@Injectable({
  providedIn: 'root'
})
export class LegalService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEscrituras() {
    return this.http.get(`${this.uri}/escrituras`);
  }

  guardarEscritura(formData: FormData) {
    return this.http.post(
      `${this.uri}/escrituras`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  obtenerEscrituraPorUuid(uuidEscritura: string) {
    return this.http.get(`${this.uri}/escrituras/${uuidEscritura}`);
  }

  modificarEscritura(uuidEscritura: string, escritura: EmpresaEscritura) {
    return this.http.put(`${this.uri}/escrituras/${uuidEscritura}`, escritura);
  }

  eliminarEscritura(uuidEscritura: string) {
    return this.http.delete(`${this.uri}/escrituras/${uuidEscritura}`);
  }

  descargarEscrituraPdf(escrituraUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/escrituras/${escrituraUuid}/pdf`, httpOptions)
  }

  // Escrituras socios
  obtenerEscrituraSocios(escrituraUuid: string) {
    return this.http.get(`${this.uri}/escrituras/${escrituraUuid}/socios`);
  }

  guardarEscrituraSocio(escrituraUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.post(`${this.uri}/escrituras/${escrituraUuid}/socios`, escrituraSocio);
  }

  modificarEscrituraSocio(escrituraUuid: string, socioUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.put(`${this.uri}/escrituras/${escrituraUuid}/socios/${socioUuid}`, escrituraSocio);
  }

  eliminarEscrituraSocio(escrituraUuid: string, socioUuid: string) {
    return this.http.delete(`${this.uri}/escrituras/${escrituraUuid}/socios/${socioUuid}`);
  }

  // Escrituras representantes
  obtenerEscrituraRepresentantes(escrituraUuid: string) {
    return this.http.get(`${this.uri}/escrituras/${escrituraUuid}/representantes`);
  }

  guardarEscrituraRepresentante(escrituraUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.post(`${this.uri}/escrituras/${escrituraUuid}/representantes`, escrituraRepresentante);
  }

  modificarEscrituraRepresentante(escrituraUuid: string, representanteUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.put(`${this.uri}/escrituras/${escrituraUuid}/representantes/${representanteUuid}`, escrituraRepresentante);
  }

  eliminarEscrituraRepresentante(escrituraUuid: string, representanteUuid: string) {
    return this.http.delete(`${this.uri}/escrituras/${escrituraUuid}/representantes/${representanteUuid}`);
  }

  // Escrituras consejos
  obtenerEscrituraConsejos(escrituraUuid: string) {
    return this.http.get(`${this.uri}/escrituras/${escrituraUuid}/consejos`);
  }

  guardarEscrituraConsejos(escrituraUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.post(`${this.uri}/escrituras/${escrituraUuid}/consejos`, escrituraConsejo);
  }

  modificarEscrituraConsejo(escrituraUuid: string, consejoUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.put(`${this.uri}/escrituras/${escrituraUuid}/consejos/${consejoUuid}`, escrituraConsejo);
  }

  eliminarEscrituraConsejo(escrituraUuid: string, consejoUuid: string) {
    return this.http.delete(`${this.uri}/escrituras/${escrituraUuid}/consejos/${consejoUuid}`)
  }

  // Escrituras apoderados
  obtenerEscriturasApoderados(escrituraUuid: string) {
    return this.http.get(`${this.uri}/escrituras/${escrituraUuid}/apoderados`);
  }

  guardarEscrituraApoderado(escrituraUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.post(`${this.uri}/escrituras/${escrituraUuid}/apoderados`, escrituraApoderado);
  }

  modificarEscrituraApoderado(escrituraUuid: string, apoderadoUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.put(`${this.uri}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}`, escrituraApoderado );
  }

  eliminarEscrituraApoderado(escrituraUuid: string, apoderadoUuid: string) {
    return this.http.delete(`${this.uri}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}`)
  }
}
