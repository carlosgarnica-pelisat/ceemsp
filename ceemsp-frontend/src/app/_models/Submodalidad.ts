import { Deserializable } from "./Deserializable";

export default class Submodalidad implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;
  descripcion?: string;
  canes: boolean;
  armas: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
