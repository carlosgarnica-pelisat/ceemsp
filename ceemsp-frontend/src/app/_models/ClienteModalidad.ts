import {Deserializable} from "./Deserializable";
import ClienteDomicilio from "./ClienteDomicilio";
import Persona from "./Persona";
import EmpresaModalidad from "./EmpresaModalidad";

export default class ClienteModalidad implements Deserializable {

  id: number;
  uuid: string;
  modalidad: EmpresaModalidad;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
