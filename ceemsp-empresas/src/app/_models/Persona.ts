import {Deserializable} from "./Deserializable";
import PersonalNacionalidad from "./PersonalNacionalidad";
import PersonaCertificacion from "./PersonaCertificacion";
import PersonalPuestoTrabajo from "./PersonalPuestoTrabajo";
import PersonalSubpuestoTrabajo from "./PersonalSubpuestoTrabajo";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Modalidad from "./Modalidad";
import Estado from "./Estado";
import Municipio from "./Municipio";
import Colonia from "./Colonia";
import Localidad from "./Localidad";
import Calle from "./Calle";
import PersonaFotografiaMetadata from "./PersonaFotografiaMetadata";
import Can from "./Can";
import Arma from "./Arma";
import Vehiculo from "./Vehiculo";
import Cliente from "./Cliente";
import ClienteDomicilio from "./ClienteDomicilio";
import EmpresaModalidad from "./EmpresaModalidad";

export default class Persona implements Deserializable {
  id: string;
  uuid: string;
  nacionalidad: PersonalNacionalidad;
  curp: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombres: string;
  sexo: string;
  fechaNacimiento: string;
  fechaIngreso: string;
  tipoSangre: string;
  estadoCivil: string;
  domicilio1: string;
  numeroExterior: string;
  numeroInterior: string;
  localidad: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  telefono: string;
  correoElectronico: string;
  rfc: string;
  eliminado: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  puestoDeTrabajo: PersonalPuestoTrabajo;
  subpuestoDeTrabajo: PersonalSubpuestoTrabajo;
  detallesPuesto: string;
  domicilioAsignado: EmpresaDomicilio;
  estatusCuip: string;
  cuip: string;
  numeroVolanteCuip: string;
  fechaVolanteCuip: string;
  modalidad: EmpresaModalidad;
  rutaVolanteCuip: string;
  formaEjecucion: string;

  estadoCatalogo: Estado;
  municipioCatalogo: Municipio;
  localidadCatalogo: Localidad;
  coloniaCatalogo: Colonia;
  calleCatalogo: Calle;

  fotografias: PersonaFotografiaMetadata[];
  certificaciones: PersonaCertificacion[];

  can: Can;
  armaCorta: Arma;
  armaLarga: Arma;
  vehiculo: Vehiculo;
  cliente: Cliente;
  clienteDomicilio: ClienteDomicilio;
  archivoVolanteCuipCargado: boolean;

  puestoTrabajoCapturado: boolean;
  cursosCapturados: boolean;
  fotografiaCapturada: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
