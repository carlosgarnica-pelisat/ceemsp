import {Deserializable} from "./Deserializable";
import Empresa from "./Empresa";
import Usuario from "./Usuario";
import Estado from "./Estado";
import Municipio from "./Municipio";
import Localidad from "./Localidad";
import Colonia from "./Colonia";
import Calle from "./Calle";
import VisitaArchivoMetadata from "./VisitaArchivoMetadata";

export default class Visita implements Deserializable {
  id: number;
  uuid: string;
  empresa: Empresa;
  tipoVisita: string;
  numeroRegistro: string;
  numeroOrden: string;
  fechaVisita: string;
  requerimiento: boolean;
  detallesRequerimiento: string;
  observaciones: string;
  fechaTermino: string;
  responsable: Usuario;
  domicilio1: string;
  numeroExterior: string;
  numeroInterior: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  nombreComercial: string;
  razonSocial: string;
  localidad: string;
  estadoCatalogo: Estado;
  municipioCatalogo: Municipio;
  localidadCatalogo: Localidad;
  coloniaCatalogo: Colonia;
  calleCatalogo: Calle;
  existeEmpresa: boolean;

  archivos: VisitaArchivoMetadata[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
