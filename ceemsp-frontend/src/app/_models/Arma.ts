import {Deserializable} from "./Deserializable";
import EmpresaLicenciaColectiva from "./EmpresaLicenciaColectiva";
import ArmaClase from "./ArmaClase";
import ArmaMarca from "./ArmaMarca";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Persona from "./Persona";
import Incidencia from "./Incidencia";
import Empresa from "./Empresa";

export default class Arma implements Deserializable {
  id: number;
  uuid: string;
  licenciaColectiva: EmpresaLicenciaColectiva;
  tipo: string;
  clase: ArmaClase;
  marca: ArmaMarca;
  calibre: string;
  bunker: EmpresaDomicilio;
  status: string;
  serie: string;
  matricula: string;
  personal: Persona;
  eliminado: boolean;
  eliminadoIncidencia: boolean;
  fechaCreacionIncidencia: string;
  fechaEliminacionIncidencia: string;
  incidencia: Incidencia;
  personalAsignado: Persona;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;
  fechaCreacion: string;
  razonBajaIncidencia: string;
  empresa: Empresa;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
