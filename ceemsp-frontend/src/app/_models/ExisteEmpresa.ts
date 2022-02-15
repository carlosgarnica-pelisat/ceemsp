import {Deserializable} from "./Deserializable";
import Empresa from "./Empresa";

export default class ExisteEmpresa implements Deserializable {

  existe: boolean;
  rfc: string;
  curp: string;
  empresa: Empresa;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
