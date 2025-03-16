import {Deserializable} from "./Deserializable";
import Persona from "./Persona";
import Empresa from "./Empresa";

export default class ExistePersona implements Deserializable {

  existe: boolean;
  curp: string;
  rfc: string;
  persona: Persona;
  empresa: Empresa;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
