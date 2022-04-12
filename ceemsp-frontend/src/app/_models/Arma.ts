import {Deserializable} from "./Deserializable";
import EmpresaLicenciaColectiva from "./EmpresaLicenciaColectiva";
import ArmaClase from "./ArmaClase";
import ArmaMarca from "./ArmaMarca";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Persona from "./Persona";

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
  personal: Persona;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
