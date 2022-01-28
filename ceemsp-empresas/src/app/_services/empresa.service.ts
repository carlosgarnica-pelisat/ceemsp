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

  // Modalidades
  obtenerModalidades(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/modalidades`)
  }

  guardarModalidad(empresaUuid: string, modalidad: EmpresaModalidad) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/modalidades`, modalidad)
  }

  // Formas de ejecucion
  obtenerFormasEjecucion(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/formas`);
  }

  guardarFormaEjecucion(empresaUuid: string, formaEjecucion: EmpresaFormaEjecucion) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/formas`, formaEjecucion);
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

  guardarEscritura(uuid: string, escritura: EmpresaEscritura) {
    return this.http.post(`${this.uri}/empresas/${uuid}/escrituras`, escritura);
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

  // Escrituras socios
  obtenerEscrituraSocios(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`);
  }

  guardarEscrituraSocio(empresaUuid: string, escrituraUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`, escrituraSocio);
  }

  // Escrituras representantes
  obtenerEscrituraRepresentantes(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`);
  }

  guardarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`, escrituraRepresentante);
  }

  // Escrituras consejos
  obtenerEscrituraConsejos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`);
  }

  guardarEscrituraConsejos(empresaUuid: string, escrituraUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`, escrituraConsejo);
  }

  // Escrituras apoderados
  obtenerEscriturasApoderados(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`);
  }

  guardarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`, escrituraApoderado);
  }

  // Licencias colectivas
  obtenerLicenciasColectivas(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias`);
  }

  obtenerLicenciaColectivaPorUuid(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}`)
  }

  guardarLicenciaColectiva(uuid: string, licenciaColectiva: EmpresaLicenciaColectiva) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias`, licenciaColectiva);
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

  // Clientes
  obtenerClientes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes`);
  }

  obtenerClientePorUuid(uuid: string, clienteUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/${clienteUuid}`)
  }

  guardarCliente(uuid: string, cliente: Cliente) {
    return this.http.post(`${this.uri}/empresas/${uuid}/clientes`, cliente)
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

  // Canes adiestramiento
  guardarCanAdiestramiento(uuid: string, canUuid: string, adiestramiento: CanAdiestramiento) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos`, adiestramiento);
  }

  // Constancias de salud
  guardarCanConstanciaSalud(uuid: string, canUuid: string, constanciaSalud: CanConstanciaSalud) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias`, constanciaSalud);
  }

  // Cartillas vacunacion
  guardarCanCartillaVacunacion(uuid: string, canUuid: string, cartillaVacunacion: CanCartillaVacunacion) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas`, cartillaVacunacion);
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

  // Usuario
  goardarUsuario(uuid: string, usuario: Usuario) {
    return this.http.post(`${this.uri}/empresas/${uuid}/usuarios`, usuario);
  }
}
