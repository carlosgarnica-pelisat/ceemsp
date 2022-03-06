import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import Empresa from "../_models/Empresa";
import EmpresaDomicilio from "../_models/EmpresaDomicilio";
import EmpresaEscritura from "../_models/EmpresaEscritura";
import Vehiculo from "../_models/Vehiculo";
import EmpresaModalidad from "../_models/EmpresaModalidad";
import EmpresaLicenciaColectiva from "../_models/EmpresaLicenciaColectiva";
import EmpresaEscrituraSocio from "../_models/EmpresaEscrituraSocio";
import EmpresaEscrituraApoderado from "../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../_models/EmpresaEscrituraRepresentante";
import Cliente from "../_models/Cliente";
import EmpresaEscrituraConsejo from "../_models/EmpresaEscrituraConsejo";
import ClienteDomicilio from "../_models/ClienteDomicilio";
import Persona from "../_models/Persona";
import Can from "../_models/Can";
import Arma from "../_models/Arma";
import VehiculoColor from "../_models/VehiculoColor";
import PersonaCertificacion from "../_models/PersonaCertificacion";
import CanAdiestramiento from "../_models/CanAdiestramiento";
import CanConstanciaSalud from "../_models/CanConstanciaSalud";
import CanCartillaVacunacion from "../_models/CanCartillaVacunacion";
import EmpresaFormaEjecucion from "../_models/EmpresaFormaEjecucion";
import Uniforme from "../_models/Uniforme";
import EmpresaUniformeElemento from "../_models/EmpresaUniformeElemento";
import EmpresaEquipo from "../_models/EmpresaEquipo";
import Usuario from "../_models/Usuario";
import Incidencia from "../_models/Incidencia";

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEmpresas() {
    return this.http.get(`${this.uri}/empresas`)
  }

  guardarEmpresa(empresa: Empresa) {
    return this.http.post(`${this.uri}/empresas`, empresa);
  }

  obtenerPorUuid(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}`);
  }

  cambiarStatusEmpresa(uuid: string, empresa: Empresa) {
    return this.http.put(`${this.uri}/empresas/${uuid}/status`, empresa);
  }

  modificarEmpresa(uuid: string, empresa: Empresa) {
    return this.http.put(`${this.uri}/empresas/${uuid}`, empresa);
  }

  // Modalidades
  obtenerModalidades(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/modalidades`)
  }

  guardarModalidad(empresaUuid: string, modalidad: EmpresaModalidad) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/modalidades`, modalidad)
  }

  eliminarModalidad(empresaUuid: string, modalidadUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/modalidades/${modalidadUuid}`);
  }

  // Formas de ejecucion
  obtenerFormasEjecucion(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/formas`);
  }

  guardarFormaEjecucion(empresaUuid: string, formaEjecucion: EmpresaFormaEjecucion) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/formas`, formaEjecucion);
  }

  eliminarFormaEjecucion(empresaUuid: string, formaEjecucionUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/formas/${formaEjecucionUuid}`);
  }

  // Domicilios
  obtenerDomicilios(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/domicilios`);
  }

  obtenerDomicilioPorUuid(uuid: string, domicilioUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}`)
  }

  guardarDomicilio(uuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/empresas/${uuid}/domicilios`, empresaDomicilio)
  }

  modificarDomicilio(uuid: string, domicilioUuid: string, empresaDomicilio: EmpresaDomicilio) {
    return this.http.put(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}`, empresaDomicilio);
  }

  eliminarDomicilio(uuid: string, domicilioUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}`);
  }

  // Escrituras
  obtenerEscrituras(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/escrituras`);
  }

  guardarEscritura(uuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/escrituras`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  obtenerEscrituraPorUuid(uuid: string, uuidEscritura: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/escrituras/${uuidEscritura}`);
  }

  modificarEscritura(uuid: string, uuidEscritura: string, escritura: EmpresaEscritura) {
    return this.http.put(`${this.uri}/empresas/${uuid}/escrituras/${uuidEscritura}`, escritura);
  }

  eliminarEscritura(uuid: string, uuidEscritura: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/escrituras/${uuidEscritura}`);
  }

  descargarEscrituraPdf(uuid: string, escrituraUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/escrituras/${escrituraUuid}/pdf`, httpOptions)
  }

  // Escrituras socios
  obtenerEscrituraSocios(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`);
  }

  guardarEscrituraSocio(empresaUuid: string, escrituraUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`, escrituraSocio);
  }

  modificarEscrituraSocio(empresaUuid: string, escrituraUuid: string, socioUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/${socioUuid}`, escrituraSocio);
  }

  eliminarEscrituraSocio(empresaUuid: string, escrituraUuid: string, socioUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/${socioUuid}`);
  }

  // Escrituras representantes
  obtenerEscrituraRepresentantes(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`);
  }

  guardarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`, escrituraRepresentante);
  }

  modificarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, representanteUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes/${representanteUuid}`, escrituraRepresentante);
  }

  eliminarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, representanteUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes/${representanteUuid}`);
  }

  // Escrituras consejos
  obtenerEscrituraConsejos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`);
  }

  guardarEscrituraConsejos(empresaUuid: string, escrituraUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`, escrituraConsejo);
  }

  modificarEscrituraConsejo(empresaUuid: string, escrituraUuid: string, consejoUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos/${consejoUuid}`, escrituraConsejo);
  }

  eliminarEscrituraConsejo(empresaUuid: string, escrituraUuid: string, consejoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos/${consejoUuid}`)
  }

  // Escrituras apoderados
  obtenerEscriturasApoderados(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`);
  }

  guardarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`, escrituraApoderado);
  }

  modificarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, apoderadoUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}`, escrituraApoderado );
  }

  eliminarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, apoderadoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}`)
  }

  // Licencias colectivas
  obtenerLicenciasColectivas(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias`);
  }

  obtenerLicenciaColectivaPorUuid(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}`)
  }

  descargarLicenciaPdf(uuid: string, licenciaColectivaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/pdf`, httpOptions)
  }

  guardarLicenciaColectiva(uuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/licencias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  obtenerDomiciliosPorLicenciaColectiva(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/domicilios`);
  }

  guardarDomicilioEnLicenciaColectiva(uuid: string, licenciaColectivaUuid: string, domicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/domicilios`, domicilio);
  }

  // Vehiculos
  obtenerVehiculos(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos`);
  }

  obtenerVehiculoPorUuid(uuid: string, vehiculoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}`);
  }

  guardarVehiculo(uuid: string, vehiculo: Vehiculo) {
    return this.http.post(`${this.uri}/empresas/${uuid}/vehiculos`, vehiculo);
  }

  guardarVehiculoColor(uuid: string, vehiculoUuid: string, color: VehiculoColor) {
    return this.http.post(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/colores`, color);
  }

  guardarVehiculoFotografia(empresaUuid: string, vehiculoUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${empresaUuid}/vehiculos/${vehiculoUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarVehiculoFotografia(empresaUuid: string, vehiculoUuid: string, fotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    }

    return this.http.get(`${this.uri}/empresas/${empresaUuid}/vehiculos/${vehiculoUuid}/fotografias/${fotografiaUuid}`, httpOptions);
  }

  // Clientes
  obtenerClientes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes`);
  }

  obtenerClientePorUuid(uuid: string, clienteUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/${clienteUuid}`)
  }

  guardarCliente(uuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/clientes`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  // Clientes domicilios
  guardarDomicilioCliente(uuidEmpresa: string, uuidCliente: string, clienteDomicilio: ClienteDomicilio[]) {
    return this.http.post(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/domicilios`, clienteDomicilio  )
  }

  // Personal
  obtenerPersonal(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/personas`)
  }

  obtenerPersonalPorUuid(uuid: string, personaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/personas/${personaUuid}`)
  }

  guardarPersonal(uuid: string, persona: Persona) {
    return this.http.post(`${this.uri}/empresas/${uuid}/personas`, persona);
  }

  modificarInformacionTrabajo(uuid: string, personaUuid: string, persona: Persona) {
    return this.http.put(`${this.uri}/empresas/${uuid}/personas/${personaUuid}/puestos`, persona)
  }

  // Personal certificaciones
  obtenerCertificacionesPersonalPorUuid(empresaUuid: string, personaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones`)
  }

  guardarPersonalCertificacion(empresaUuid: string, personaUuid: string, certificacion: PersonaCertificacion) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones`, certificacion);
  }

  // Personal fotografias
  guardarPersonaFotografia(empresaUuid: string, personaUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarPersonaFotografia(empresaUuid: string, personaUuid: string, fotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    }

    return this.http.get(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/fotografias/${fotografiaUuid}`, httpOptions);
  }

  // Canes
  obtenerCanes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes`);
  }

  obtenerCanPorUuid(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}`);
  }

  guardarCan(uuid: string, can: Can) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes`, can);
  }

  guardarCanFotografia(empresaUuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${empresaUuid}/canes/${canUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  // Canes adiestramiento
  guardarCanAdiestramiento(uuid: string, canUuid: string, adiestramiento: CanAdiestramiento) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos`, adiestramiento);
  }

  // Constancias de salud
  guardarCanConstanciaSalud(uuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  // Cartillas vacunacion
  guardarCanCartillaVacunacion(uuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  // Armas
  obtenerArmasPorLicenciaColectivaUuid(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas`);
  }

  guardarArma(uuid: string, licenciaColectivaUuid: string, arma: Arma) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas`, arma);
  }

  // equipo
  obtenerEquipos(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/equipos`);
  }

  guardarEquipo(uuid: string, equipo: EmpresaEquipo) {
    return this.http.post(`${this.uri}/empresas/${uuid}/equipos`, equipo);
  }

  // Uniformes
  obtenerUniformes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes`);
  }

  obtenerUniformePorUuid(uuid: string, uniformeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}`);
  }

  guardarUniforme(uuid: string, uniforme: Uniforme) {
    return this.http.post(`${this.uri}/empresas/${uuid}/uniformes`, uniforme);
  }

  guardarUniformeElemento(uuid: string, uniformeUuid: string, elementoUniforme: EmpresaUniformeElemento) {
    return this.http.post(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/elementos`, elementoUniforme);
  }

  // Incidencias
  obtenerIncidenciasPorEmpresa(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias`);
  }

  obtenerIncidenciaPorUuid(uuid: string, incidenciaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}`);
  }

  guardarIncidencia(uuid: string, incidencia: Incidencia) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias`, incidencia);
  }

  // Usuario
  goardarUsuario(uuid: string, usuario: Usuario) {
    return this.http.post(`${this.uri}/empresas/${uuid}/usuarios`, usuario);
  }
}
