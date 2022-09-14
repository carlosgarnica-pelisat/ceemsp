import {Deserializable} from "./Deserializable";
import EmpresaLicenciaColectiva from "./EmpresaLicenciaColectiva";
import ArmaClase from "./ArmaClase";
import ArmaMarca from "./ArmaMarca";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Persona from "./Persona";
import Incidencia from "./Incidencia";

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
  incidencia: Incidencia;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
