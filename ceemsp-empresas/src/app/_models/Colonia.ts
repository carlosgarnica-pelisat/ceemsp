import {Deserializable} from "./Deserializable";

export default class Colonia implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  codigoPostal: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
