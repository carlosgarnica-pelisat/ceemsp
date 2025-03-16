import {Deserializable} from "./Deserializable";
import EmpresaEscritura from "./EmpresaEscritura";

export default class ExisteEscritura implements Deserializable {

  existe: boolean;
  numero: string;
  escritura: EmpresaEscritura;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
