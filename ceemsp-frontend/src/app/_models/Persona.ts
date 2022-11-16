import {Deserializable} from "./Deserializable";
import PersonalNacionalidad from "./PersonalNacionalidad";
import PersonaCertificacion from "./PersonaCertificacion";
import PersonalPuestoTrabajo from "./PersonalPuestoTrabajo";
import PersonalSubpuestoTrabajo from "./PersonalSubpuestoTrabajo";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Modalidad from "./Modalidad";
import PersonaFotografiaMetadata from "./PersonaFotografiaMetadata";
import Estado from "./Estado";
import Municipio from "./Municipio";
import Localidad from "./Localidad";
import Colonia from "./Colonia";
import Calle from "./Calle";

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
  modalidad: Modalidad;
  rutaVolanteCuip: string;

  estadoCatalogo: Estado;
  municipioCatalogo: Municipio;
  localidadCatalogo: Localidad;
  coloniaCatalogo: Colonia;
  calleCatalogo: Calle;

  fotografias: PersonaFotografiaMetadata[];
  certificaciones: PersonaCertificacion[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
