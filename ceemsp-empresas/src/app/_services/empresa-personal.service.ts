import { Injectable } from '@angular/core';
import Persona from "../_models/Persona";
import PersonaCertificacion from "../_models/PersonaCertificacion";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import PersonalCan from "../_models/PersonalCan";
import PersonalVehiculo from "../_models/PersonalVehiculo";
import PersonalArma from "../_models/PersonalArma";

@Injectable({
  providedIn: 'root'
})
export class EmpresaPersonalService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerPersonal() {
    return this.http.get(`${this.uri}/personas`)
  }

  obtenerPersonalSinAsignar() {
    return this.http.get(`${this.uri}/personas/no-asignados`)
  }

  obtenerPersonalPorUuid(personaUuid: string) {
    return this.http.get(`${this.uri}/personas/${personaUuid}`)
  }

  guardarPersonal(persona: Persona) {
    return this.http.post(`${this.uri}/personas`, persona);
  }

  modificarInformacionTrabajo(personaUuid: string, persona: FormData) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/puestos`, persona, {
      headers: {'X-isFile': 'true'}
    })
  }

  modificarPersonal(personaUuid: string, persona: Persona) {
    return this.http.put(`${this.uri}/personas/${personaUuid}`, persona);
  }

  eliminarPersonal(personaUuid: string, persona: FormData) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/borrar`, persona, {
      headers: {'X-isFile': 'true'}
    });
  }

  // Personal certificaciones
  obtenerCertificacionesPersonalPorUuid(personaUuid: string) {
    return this.http.get(`${this.uri}/personas/${personaUuid}/certificaciones`)
  }

  descargarCertificacionPdf(personaUuid: string, certificacionUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/personas/${personaUuid}/certificaciones/${certificacionUuid}/pdf`, httpOptions)
  }

  guardarPersonalCertificacion(personaUuid: string, certificacion: FormData) {
    return this.http.post(
      `${this.uri}/personas/${personaUuid}/certificaciones`,
      certificacion, {headers: {'X-isFile': 'true'}})
  }

  modificarPersonalCertificacion(personaUuid: string, capacitacionUuid: string, certificacion: FormData) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/certificaciones/${capacitacionUuid}`,
      certificacion, {headers: {'X-isFile': 'true'}});
  }

  eliminarPersonalCertificacion(personaUuid: string, capacitacionUuid: string) {
    return this.http.delete(`${this.uri}/personas/${personaUuid}/certificaciones/${capacitacionUuid}`)
  }

  // Personal fotografias
  listarPersonaFotografias(personaUuid: string) {
    return this.http.get(`${this.uri}/personas/${personaUuid}/fotografias`)
  }

  guardarPersonaFotografia(personaUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/personas/${personaUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarPersonaFotografia(personaUuid: string, fotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    }

    return this.http.get(`${this.uri}/personas/${personaUuid}/fotografias/${fotografiaUuid}`, httpOptions);
  }

  eliminarPersonaFotografia(personaUuid: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/personas/${personaUuid}/fotografias/${fotografiaUuid}`)
  }

  descargarVolanteCuip(personaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/personas/${personaUuid}/volante`, httpOptions);
  }

  asignarCanPersona(personaUuid: string, personaCan: PersonalCan) {
    return this.http.post(`${this.uri}/personas/${personaUuid}/canes`, personaCan)
  }

  desasignarCanPersona(personaUuid: string, personaCan: PersonalCan) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/canes`, personaCan)
  }

  asignarVehiculoPersona(personaUuid: string, personaVehiculo: PersonalVehiculo) {
    return this.http.post(`${this.uri}/personas/${personaUuid}/vehiculos`, personaVehiculo)
  }

  desasignarVehiculoPersona(personaUuid: string, personaVehiculo: PersonalVehiculo) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/vehiculos`, personaVehiculo)
  }

  asignarArmaCortaPersona(personaUuid: string, personaArma: PersonalArma) {
    return this.http.post(`${this.uri}/personas/${personaUuid}/armas/cortas`, personaArma)
  }

  desasignarArmaCortaPersona(personaUuid: string, personaArma: PersonalArma) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/armas/cortas`, personaArma)
  }

  asignarArmaLargaPersona(personaUuid: string, personaArma: PersonalArma) {
    return this.http.post(`${this.uri}/personas/${personaUuid}/armas/largas`, personaArma)
  }

  desasignarArmaLargaPersona(personaUuid: string, personaArma: PersonalArma) {
    return this.http.put(`${this.uri}/personas/${personaUuid}/armas/largas`, personaArma)
  }
}
