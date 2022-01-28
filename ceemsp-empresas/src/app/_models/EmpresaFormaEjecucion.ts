import {Deserializable} from "./Deserializable";

export default class EmpresaFormaEjecucion implements Deserializable {
  formaEjecucion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
