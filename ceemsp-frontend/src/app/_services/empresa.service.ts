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
import {formatDate} from "@angular/common";
import Acuerdo from "../_models/Acuerdo";
import ClienteAsignacionPersonal from "../_models/ClienteAsignacionPersonal";
import ClienteModalidad from "../_models/ClienteModalidad";
import IncidenciaComentario from "../_models/IncidenciaComentario";
import EmpresaDomicilioTelefono from "../_models/EmpresaDomicilioTelefono";

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {

  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerEmpresas() {
    return this.http.get(`${this.uri}/empresas`)
  }

  guardarEmpresa(empresa: FormData) {
    return this.http.post(`${this.uri}/empresas`, empresa, {
        headers: {'X-isFile': 'true'}
      });
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

  // Acuerdos
  obtenerAcuerdos(empresaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/acuerdos`)
  }

  obtenerAcuerdoPorUuid(empresaUuid: string, acuerdoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/acuerdos/${acuerdoUuid}`)
  }

  descargarAcuerdo(empresaUuid: string, acuerdoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/acuerdos/${acuerdoUuid}/archivo`, httpOptions);
  }

  guardarAcuerdo(empresaUuid: string, acuerdo: FormData) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/acuerdos`, acuerdo,  {
      headers: {'X-isFile': 'true'}
    })
  }

  modificarAcuerdo(empresaUuid: string, acuerdoUuid: string, acuerdo: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/acuerdos/${acuerdoUuid}`, acuerdo, {
      headers: {'X-isFile': 'true'}
    })
  }

  eliminarAcuerdo(empresaUuid: string, acuerdoUuid: string, acuerdo: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/acuerdos/${acuerdoUuid}/borrar`, acuerdo, {
      headers: {'X-isFile': 'true'}
    })
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

  obtenerDomiciliosEliminados(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/domicilios/eliminados`);
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

  eliminarDomicilio(uuid: string, domicilioUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}/borrar`, formData, {
        headers: {'X-isFile': 'true'}
      })
  }

  // Telefonos de dommicilios
  obtenerTelefonosPorDomicilio(uuid: string, domicilioUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}/telefonos`);
  }

  guardarTelefonoDomicilio(uuid: string, domicilioUuid: string, telefono: EmpresaDomicilioTelefono) {
    return this.http.post(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}/telefonos`, telefono);
  }

  modificarTelefonoDomicilio(uuid: string, domicilioUuid: string, telefonoUuid: string, telefono: EmpresaDomicilioTelefono) {
    return this.http.put(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}/telefonos/${telefonoUuid}`, telefono);
  }

  eliminarTelefonoDomicilio(uuid: string, domicilioUuid: string, telefonoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/domicilios/${domicilioUuid}/telefonos/${telefonoUuid}`);
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

  modificarEscritura(uuid: string, uuidEscritura: string, escritura: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/escrituras/${uuidEscritura}`, escritura,
      {headers: {'X-isFile': 'true'}});
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

  obtenerEscrituraSociosTodos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/todos`)
  }

  guardarEscrituraSocio(empresaUuid: string, escrituraUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios`, escrituraSocio);
  }

  modificarEscrituraSocio(empresaUuid: string, escrituraUuid: string, socioUuid: string, escrituraSocio: EmpresaEscrituraSocio) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/${socioUuid}`, escrituraSocio);
  }

  eliminarEscrituraSocio(empresaUuid: string, escrituraUuid: string, socioUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/${socioUuid}/borrar`, formData, {headers: {'X-isFile': 'true'}});
  }

  descargarDocumentoFundatorioSocio(empresaUuid: string, escrituraUuid: string, socioUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/socios/${socioUuid}/documentos/fundatorios`, httpOptions)
  }

  // Escrituras representantes
  obtenerEscrituraRepresentantes(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`);
  }

  obtenerEscrituraRepresentantesTodos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes/todos`);
  }

  guardarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes`, escrituraRepresentante);
  }

  modificarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, representanteUuid: string, escrituraRepresentante: EmpresaEscrituraRepresentante) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes/${representanteUuid}`, escrituraRepresentante);
  }

  eliminarEscrituraRepresentante(empresaUuid: string, escrituraUuid: string, representanteUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/representantes/${representanteUuid}/borrar`, formData, {headers: {'X-isFile': 'true'}});
  }

  // Escrituras consejos
  obtenerEscrituraConsejos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`);
  }

  obtenerEscrituraConsejosTodos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos/todos`);
  }

  guardarEscrituraConsejos(empresaUuid: string, escrituraUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos`, escrituraConsejo);
  }

  modificarEscrituraConsejo(empresaUuid: string, escrituraUuid: string, consejoUuid: string, escrituraConsejo: EmpresaEscrituraConsejo) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos/${consejoUuid}`, escrituraConsejo);
  }

  eliminarEscrituraConsejo(empresaUuid: string, escrituraUuid: string, consejoUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/consejos/${consejoUuid}/borrar`, formData, {headers: {'X-isFile': 'true'}})
  }

  // Escrituras apoderados
  obtenerEscriturasApoderados(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`);
  }

  obtenerEscriturasApoderadosTodos(empresaUuid: string, escrituraUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados/todos`);
  }

  guardarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.post(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados`, escrituraApoderado);
  }

  modificarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, apoderadoUuid: string, escrituraApoderado: EmpresaEscrituraApoderado) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}`, escrituraApoderado );
  }

  eliminarEscrituraApoderado(empresaUuid: string, escrituraUuid: string, apoderadoUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/escrituras/${escrituraUuid}/apoderados/${apoderadoUuid}/borrar`, formData, {headers: {'X-isFile': 'true'}})
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

  modificarLicenciaColectiva(uuid: string, licenciaColectivaUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}`, formData, {headers: {'X-isFile': 'true'}})
  }

  eliminarLicenciaColectiva(uuid: string, licenciaColectivaUuid: string, formData: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/borrar`, formData, {headers: {'X-isFile': 'true'}})
  }

  obtenerDomiciliosPorLicenciaColectiva(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/domicilios`);
  }

  guardarDomicilioEnLicenciaColectiva(uuid: string, licenciaColectivaUuid: string, domicilio: EmpresaDomicilio) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/domicilios`, domicilio);
  }

  eliminarDomicilioEnLicenciaColectiva(uuid: string, licenciaColectivaUuid: string, domicilioUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/domicilios/${domicilioUuid}`)
  }

  // Vehiculos
  obtenerVehiculos(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos`);
  }

  obtenerVehiculosEliminados(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos/eliminados`)
  }

  obtenerVehiculoPorUuid(uuid: string, vehiculoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}`);
  }

  descargarVehiculoConstanciaPdf(uuid: string, vehiculoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/pdf`, httpOptions)
  }

  guardarVehiculo(uuid: string, vehiculo: FormData) {
    return this.http.post(`${this.uri}/empresas/${uuid}/vehiculos`, vehiculo, {
      headers: {'X-isFile': 'true'}
    });
  }

  modificarVehiculo(uuid: string, vehiculoUuid: string, vehiculo: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}`, vehiculo, {
      headers: {'X-isFile': 'true'}
    });
  }

  eliminarVehiculo(uuid: string, vehiculoUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/borrar`, formData,{
        headers: {'X-isFile': 'true'}
      })
  }

  obtenerVehiculoColores(uuid: string, vehiculoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/colores`)
  }

  guardarVehiculoColor(uuid: string, vehiculoUuid: string, color: VehiculoColor) {
    return this.http.post(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/colores`, color);
  }

  modificarVehiculoColor(uuid: string, vehiculoUuid: string, colorUuid: string, color: VehiculoColor) {
    return this.http.put(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/colores/${colorUuid}`, color);
  }

  eliminarVehiculoColor(uuid: string, vehiculoUuid: string, colorUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/vehiculos/${vehiculoUuid}/colores/${colorUuid}`);
  }

  listarVehiculoFotografias(empresaUuid: string, vehiculoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/vehiculos/${vehiculoUuid}/fotografias`);
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

  eliminarVehiculoFotografia(empresaUuid: string, vehiculoUuid: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/vehiculos/${vehiculoUuid}/fotografias/${fotografiaUuid}`);
  }

  // Clientes
  obtenerClientes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes`);
  }

  obtenerClientesEliminados(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/eliminados`);
  }

  obtenerClientePorUuid(uuid: string, clienteUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/${clienteUuid}`)
  }

  obtenerClienteContrato(uuid: string, clienteUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/clientes/${clienteUuid}/pdf`, httpOptions);
  }

  guardarCliente(uuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/clientes`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  modificarCliente(uuidEmpresa: string, uuidCliente: string, cliente: Cliente) {
    return this.http.put(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}`, cliente);
  }

  eliminarCliente(uuidEmpresa: string, uuidCliente: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/borrar`, formData,{
        headers: {'X-isFile': 'true'}
      })
  }

  // Clientes domicilios
  obtenerClienteDomicilios(uuidEmpresa: string, uuidCliente: string) {
    return this.http.get(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/domicilios`)
  }

  guardarDomicilioCliente(uuidEmpresa: string, uuidCliente: string, clienteDomicilio: ClienteDomicilio[]) {
    return this.http.post(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/domicilios`, clienteDomicilio)
  }

  modificarDomicilioCliente(uuidEmpresa: string, uuidCliente: string, uuidDomicilio: string, clienteDomicilio: ClienteDomicilio) {
    return this.http.put(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`, clienteDomicilio);
  }

  eliminarDomicilioCliente(uuidEmpresa: string, uuidCliente: string, uuidDomicilio: string) {
    return this.http.delete(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/domicilios/${uuidDomicilio}`)
  }

  // Asignacion clientes
  obtenerAsignacionesCliente(uuidEmpresa: string, uuidCliente: string) {
    return this.http.get(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/asignaciones`)
  }

  guardarAsignacionCliente(uuidEmpresa: string, uuidCliente: string, asignacionCliente: ClienteAsignacionPersonal) {
    return this.http.post(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/asignaciones`, asignacionCliente)
  }

  modificarAsignacionCliente(uuidEmpresa: string, uuidCliente: string, uuidAsignacion: string, asignacionCliente: ClienteAsignacionPersonal) {
    return this.http.put(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/asignaciones/${uuidAsignacion}`, asignacionCliente);
  }

  eliminarAsignacionCliente(uuidEmpresa: string, uuidCliente: string, uuidAsignacion: string) {
    return this.http.delete(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/asignaciones/${uuidAsignacion}`);
  }

  // Clientes modalidades
  obtenerModalidadesCliente(uuidEmpresa: string, uuidCliente: string) {
    return this.http.get(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/modalidades`);
  }

  guardarModalidadCliente(uuidEmpresa: string, uuidCliente: string, modalidadCliente: ClienteModalidad) {
    return this.http.post(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/modalidades`, modalidadCliente)
  }

  modificarModalidadCliente(uuidEmpresa: string, uuidCliente: string, uuidModalidad: string, modalidadCliente: ClienteModalidad) {
    return this.http.put(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/modalidades/${uuidModalidad}`, modalidadCliente);
  }

  eliminarModalidadCliente(uuidEmpresa: string, uuidCliente: string, uuidModalidad: string) {
    return this.http.delete(`${this.uri}/empresas/${uuidEmpresa}/clientes/${uuidCliente}/modalidades/${uuidModalidad}`)
  }

  // Personal
  obtenerPersonal(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/personas`)
  }

  obtenerPersonalEliminado(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/personas/eliminados`)
  }

  obtenerPersonalPorUuid(uuid: string, personaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/personas/${personaUuid}`)
  }

  guardarPersonal(uuid: string, persona: Persona) {
    return this.http.post(`${this.uri}/empresas/${uuid}/personas`, persona);
  }

  modificarInformacionTrabajo(uuid: string, personaUuid: string, persona: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/personas/${personaUuid}/puestos`, persona, {
      headers: {'X-isFile': 'true'}
    })
  }

  modificarPersonal(uuid: string, personaUuid: string, persona: Persona) {
    return this.http.put(`${this.uri}/empresas/${uuid}/personas/${personaUuid}`, persona);
  }

  eliminarPersonal(uuid: string, personaUuid: string,  persona: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/personas/${personaUuid}`, persona, {
      headers: {'X-isFile': 'true'}
    });
  }

  descargarVolanteCuip(uuid: string, personaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/personas/${personaUuid}/volante`, httpOptions);
  }

  // Personal certificaciones
  obtenerCertificacionesPersonalPorUuid(empresaUuid: string, personaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones`)
  }

  descargarCertificacionPdf(uuid: string, personaUuid: string, certificacionUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/personas/${personaUuid}/certificaciones/${certificacionUuid}/pdf`, httpOptions)
  }

  guardarPersonalCertificacion(empresaUuid: string, personaUuid: string, certificacion: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones`,
      certificacion, {headers: {'X-isFile': 'true'}})
  }

  modificarPersonalCertificacion(empresaUuid: string, personaUuid: string, capacitacionUuid: string, certificacion: FormData) {
    return this.http.put(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones/${capacitacionUuid}`,
      certificacion, {headers: {'X-isFile': 'true'}});
  }

  eliminarPersonalCertificacion(empresaUuid: string, personaUuid: string, capacitacionUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/certificaciones/${capacitacionUuid}`)
  }

  // Personal fotografias
  listarPersonaFotografias(empresaUuid: string, personaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/fotografias`)
  }

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

  eliminarPersonaFotografia(empresaUuid: string, personaUuid: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/personas/${personaUuid}/fotografias/${fotografiaUuid}`)
  }

  // Canes
  obtenerCanes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes`);
  }

  obtenerCanesEliminados(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/eliminados`)
  }

  obtenerCanPorUuid(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}`);
  }

  guardarCan(uuid: string, can: Can) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes`, can);
  }

  modificarCan(uuid: string, canUuid: string, can: Can) {
    return this.http.put(`${this.uri}/empresas/${uuid}/canes/${canUuid}`, can);
  }

  eliminarCan(uuid: string, canUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/borrar`, formData, {
        headers: {'X-isFile': 'true'}
      })
  }

  listarCanFotografias(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/fotografias`)
  }

  descargarCanFotografia(uuid: string, canUuid: string, canFotografiaUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/fotografias/${canFotografiaUuid}`, httpOptions)
  }

  guardarCanFotografia(empresaUuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${empresaUuid}/canes/${canUuid}/fotografias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  eliminarCanFotografia(empresaUuid: string, canFotografia: string, fotografiaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${empresaUuid}/canes/${canFotografia}/fotografias/${fotografiaUuid}`);
  }

  // Canes adiestramiento
  descargarCanAdiestramientos(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos`)
  }

  obtenerCanAdiestramientosTodos(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos/todos`)
  }

  guardarCanAdiestramiento(uuid: string, canUuid: string, adiestramiento: CanAdiestramiento) {
    return this.http.post(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos`, adiestramiento);
  }

  modificarCanEntrenamiento(uuid: string, canUuid: string, entrenamientoUuid: string, adiestramiento: CanAdiestramiento) {
    return this.http.put(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos/${entrenamientoUuid}`, adiestramiento);
  }

  eliminarCanEntrenamiento(uuid: string, canUuid: string, entrenamientoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/canes/${canUuid}/adiestramientos/${entrenamientoUuid}`)
  }

  // Constancias de salud
  descargarCanConstanciasSalud(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias`)
  }

  obtenerCanConstanciasSaludTodos(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias/todos`)
  }

  guardarCanConstanciaSalud(uuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarCanConstanciaSalud(uuid: string, canUuid: string, constanciaSaludUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias/${constanciaSaludUuid}/pdf`, httpOptions)
  }

  modificarCanConstanciaSalud(uuid: string, canUuid: string, canConstanciaUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias/${canConstanciaUuid}`,
      formData, {headers: {'X-isFile': 'true'}}
    )
  }

  eliminarCanConstanciaSalud(uuid: string, canUuid: string, canConstanciaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/canes/${canUuid}/constancias/${canConstanciaUuid}`);
  }

  // Cartillas vacunacion
  obtenerCanCartillasVacunacion(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas`);
  }

  obtenerCanCartillasVacunacionTodos(uuid: string, canUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas/todos`)
  }

  guardarCanCartillaVacunacion(uuid: string, canUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  descargarCanCartillaVacunacionPdf(uuid: string, canUuid: string, cartillaVacunacionUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}/pdf`, httpOptions)
  }

  modificarCanCartillaVacunacion(uuid: string, canUuid: string, cartillaVacunacionUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}`,
      formData, {headers: {'X-isFile': 'true'}}
    )
  }

  eliminarCanCartillaVacunacion(uuid: string, canUuid: string, cartillaVacunacionUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/canes/${canUuid}/cartillas/${cartillaVacunacionUuid}`)
  }

  // Armas
  obtenerArmas(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/armas`);
  }

  obtenerArmasPorLicenciaColectivaUuid(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas`);
  }

  obtenerArmasEliminadasPorLicenciaColectivaUuid(uuid: string, licenciaColectivaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas/todas`)
  }

  guardarArma(uuid: string, licenciaColectivaUuid: string, arma: Arma) {
    return this.http.post(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas`, arma);
  }

  modificarArma(uuid: string, licenciaColectivaUuid: string, armaUuid: string, arma: Arma) {
    return this.http.put(`${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}`, arma)
  }

  custodiaArma(uuid: string, licenciaColectivaUuid: string, armaUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}/custodia`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  eliminarArma(uuid: string, licenciaColectivaUuid: string, armaUuid: string, formData: FormData) {
    return this.http.put(
      `${this.uri}/empresas/${uuid}/licencias/${licenciaColectivaUuid}/armas/${armaUuid}/borrar`, formData, {
        headers: {'X-isFile': 'true'}
      })
  }

  // equipo
  obtenerEquipos(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/equipos`);
  }

  obtenerEquipoPorUuid(uuid: string, equipoUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/equipos/${equipoUuid}`)
  }

  guardarEquipo(uuid: string, equipo: EmpresaEquipo) {
    return this.http.post(`${this.uri}/empresas/${uuid}/equipos`, equipo);
  }

  modificarEquipo(uuid: string, equipoUuid: string, equipo: EmpresaEquipo) {
    return this.http.put(`${this.uri}/empresas/${uuid}/equipos/${equipoUuid}`, equipo)
  }

  eliminarEquipo(uuid: string, equipoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/equipos/${equipoUuid}`);
  }

  // Uniformes
  obtenerUniformes(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes`);
  }

  obtenerUniformePorUuid(uuid: string, uniformeUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}`);
  }

  descargarFotografiaUniforme(uuid: string, uniformeUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/fotos`, httpOptions)
  }

  guardarUniforme(uuid: string, uniforme: FormData) {
    return this.http.post(`${this.uri}/empresas/${uuid}/uniformes`, uniforme, {
      headers: {'X-isFile': 'true'}
    });
  }

  modificarUniforme(uuid: string, uuidUniforme: string, uniforme: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/uniformes/${uuidUniforme}`, uniforme, {
      headers: {'X-isFile': 'true'}
    });
  }

  eliminarUniforme(uuid: string, uuidUniforme: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/uniformes/${uuidUniforme}`);
  }

  descargarFotografiaUniformeElemento(uuid: string, uniformeUuid: string, elementoUniformeUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/elementos/${elementoUniformeUuid}/descargar`, httpOptions);
  }

  guardarUniformeElemento(uuid: string, uniformeUuid: string, elementoUniforme: FormData) {
    return this.http.post(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/elementos`, elementoUniforme, {
      headers: {'X-isFile': 'true'}
    });
  }

  modificarUniformeElemento(uuid: string, uniformeUuid: string, elementoUuid: string, elementoUniforme: FormData) {
    return this.http.put(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/elementos/${elementoUuid}`, elementoUniforme, {
      headers: {'X-isFile': 'true'}
    })
  }

  eliminarUniformeElemento(uuid: string, uniformeUuid: string, elementoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/uniformes/${uniformeUuid}/elementos/${elementoUuid}`)
  }

  // Incidencias
  obtenerIncidenciasPorEmpresa(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias`);
  }

  obtenerIncidenciaPorUuid(uuid: string, incidenciaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}`);
  }

  guardarIncidencia(uuid: string, incidencia: FormData) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias`, incidencia, {
      headers: {'X-isFile': 'true'}});
  }

  autoasignarIncidencia(uuid: string, incidenciaUuid: string) {
    return this.http.put(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/autoasignar`, {})
  }

  asignarIncidencia(uuid: string, incidenciaUuid: string, usuario: Usuario) {
    return this.http.put(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/asignar`, usuario);
  }

  agregarComentario(uuid: string, incidenciaUuid: string, incidencia: Incidencia) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/comentarios`, incidencia);
  }

  obtenerPersonasIncidencia(uuid: string, incidenciaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/personal`)
  }

  agregarPersonaIncidencia(uuid: string, incidenciaUuid: string, personal: Persona) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/personal`, personal)
  }

  agregarArmaIncidencia(uuid: string, incidenciaUuid: string, arma: Arma) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/armas`, arma)
  }

  agregarVehiculoIncidencia(uuid: string, incidenciaUuid: string, vehiculo: Vehiculo) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/vehiculos`, vehiculo)
  }

  agregarCanIncidencia(uuid: string, incidenciaUuid: string, can: Can) {
    return this.http.post(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/canes`, can)
  }

  eliminarPersonaIncidencia(uuid: string, incidenciaUuid: string, incidenciaPersonaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/personal/${incidenciaPersonaUuid}`)
  }

  eliminarArmaIncidencia(uuid: string, incidenciaUuid: string, incidenciaArmaUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/armas/${incidenciaArmaUuid}`)
  }

  eliminarVehiculoIncidencia(uuid: string, incidenciaUuid: string, incidenciaVehiculoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/vehiculos/${incidenciaVehiculoUuid}`)
  }

  eliminarCanIncidencia(uuid: string, incidenciaUuid: string, incidenciaCanUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/canes/${incidenciaCanUuid}`)
  }

  eliminarArchivoIncidencia(uuid: string, incidenciaUuid: string, incidenciaArchivoUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/archivos/${incidenciaArchivoUuid}`)
  }

  descargarArchivoIncidencia(uuid: string, incidenciaUuid: string, incidenciaArchivoUuid: string) {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/archivos/${incidenciaArchivoUuid}/descargar`, httpOptions)
  }

  agregarArchivoIncidencia(uuid: string, incidenciaUuid: string, formData: FormData) {
    return this.http.post(
      `${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/archivos`,
      formData, {headers: {'X-isFile': 'true'}})
  }

  obtenerComentariosIncidencia(uuid: string, incidenciaUuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/comentarios`)
  }

  modificarComentarioIncidencia(uuid: string, incidenciaUuid: string, comentarioUuid: string, comentario: IncidenciaComentario) {
    return this.http.put(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/comentarios/${comentarioUuid}`, comentario)
  }

  eliminarComentarioIncidencia(uuid: string, incidenciaUuid: string, comentarioUuid: string) {
    return this.http.delete(`${this.uri}/empresas/${uuid}/incidencias/${incidenciaUuid}/comentarios/${comentarioUuid}`);
  }

  // Usuario
  goardarUsuario(uuid: string, usuario: Usuario) {
    return this.http.post(`${this.uri}/empresas/${uuid}/usuarios`, usuario);
  }

  // Visitas
  obtenerVisitas(uuid: string) {
    return this.http.get(`${this.uri}/empresas/${uuid}/visitas`);
  }
}
