import {Deserializable} from "./Deserializable";
import EmpresaFormaEjecucion from "./EmpresaFormaEjecucion";

export default class ClienteFormaEjecucion implements Deserializable {

  id: number;
  uuid: string;
  formaEjecucion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
