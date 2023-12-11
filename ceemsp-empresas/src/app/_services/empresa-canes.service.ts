import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Can from "../_models/Can";
import CanAdiestramiento from "../_models/CanAdiestramiento";

@Injectable({
  providedIn: 'root'
})
export class EmpresaCanesService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerCanes() {
    return this.http.get(`${this.uri}/canes`);
  }

  obtenerCanesInstalaciones() {
    return this.http.get(`${this.uri}/canes/instalaciones`);
  }

  obtenerCanPorUuid(canUuid: string) {
    return this.http.get(`${this.uri}/canes/${canUuid}`);
  }

  guardarCan(can: Can) {
    return this.http.post(`${this.uri}/canes`, can);
  }

  modificarCan(canUuid: string, can: Can) {
    return this.http.put(`${this.uri}/canes/${canUuid}`, can);
  }

  eliminarCan(canUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/canes/${canUuid}/borrar`, formData, {
        headers: {'X-isFile': 'true'}
      })
  }

  listarCanFotografias(canUuid: string) {
    return this.http.get(`${this.uri}/canes/${canUuid}/fotografias`)
  }

  descargarCanFotografia(canUuid: string, canFotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/canes/${canUuid}/fotografias/${canFotografiaUuid}`, httpOptions)
  }

  guardarCanFotografia(canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/canes/${canUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  eliminarCanFotografia(canFotografia: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/canes/${canFotografia}/fotografias/${fotografiaUuid}`);
  }

  // Canes adiestramiento
  descargarCanAdiestramientos(canUuid: string) {
    return this.http.get(`${this.uri}/canes/${canUuid}/adiestramientos`)
  }

  guardarCanAdiestramiento(canUuid: string, adiestramiento: FormData) {
    return this.http.post(`${this.uri}/canes/${canUuid}/adiestramientos`, adiestramiento, {headers: {'X-isFile': 'true'}});
  }

  modificarCanEntrenamiento(canUuid: string, entrenamientoUuid: string, adiestramiento: FormData) {
    return this.http.put(`${this.uri}/canes/${canUuid}/adiestramientos/${entrenamientoUuid}`, adiestramiento, {headers: {'X-isFile': 'true'}});
  }

  eliminarCanEntrenamiento(canUuid: string, entrenamientoUuid: string) {
    return this.http.delete(`${this.uri}/canes/${canUuid}/adiestramientos/${entrenamientoUuid}`)
  }

  descargarCanAdiestramiento(canUuid: string, entrenamientoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/canes/${canUuid}/adiestramientos/${entrenamientoUuid}/pdf`, httpOptions)
  }

  // Constancias de salud
  descargarCanConstanciasSalud(canUuid: string) {
    return this.http.get(`${this.uri}/canes/${canUuid}/constancias`)
  }

  guardarCanConstanciaSalud(canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/canes/${canUuid}/constancias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarCanConstanciaSalud(canUuid: string, constanciaSaludUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/canes/${canUuid}/constancias/${constanciaSaludUuid}/pdf`, httpOptions)
  }

  modificarCanConstanciaSalud(canUuid: string, canConstanciaUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/canes/${canUuid}/constancias/${canConstanciaUuid}`,
      formData, {headers: {'X-isFile': 'true'}}
    )
  }

  eliminarCanConstanciaSalud(canUuid: string, canConstanciaUuid: string) {
    return this.http.delete(`${this.uri}/canes/${canUuid}/constancias/${canConstanciaUuid}`);
  }

  // Cartillas vacunacion
  obtenerCanCartillasVacunacion(canUuid: string) {
    return this.http.get(`${this.uri}/canes/${canUuid}/cartillas`);
  }

  guardarCanCartillaVacunacion(canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/canes/${canUuid}/cartillas`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarCanCartillaVacunacionPdf(canUuid: string, cartillaVacunacionUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}/pdf`, httpOptions)
  }

  modificarCanCartillaVacunacion(canUuid: string, cartillaVacunacionUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}`,
      formData, {headers: {'X-isFile': 'true'}}
    )
  }

  eliminarCanCartillaVacunacion(canUuid: string, cartillaVacunacionUuid: string) {
    return this.http.delete(`${this.uri}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}`)
  }
}
