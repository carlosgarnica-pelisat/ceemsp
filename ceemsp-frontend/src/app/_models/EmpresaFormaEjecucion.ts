import {Deserializable} from "./Deserializable";

export default class EmpresaFormaEjecucion implements Deserializable {
  id: number;
  uuid: string;
  formaEjecucion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
