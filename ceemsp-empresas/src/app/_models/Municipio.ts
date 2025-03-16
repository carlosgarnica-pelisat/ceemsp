import {Deserializable} from "./Deserializable";

export default class Municipio implements Deserializable {
  id: number;
  uuid: string;
  clave: number;
  nombre: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
