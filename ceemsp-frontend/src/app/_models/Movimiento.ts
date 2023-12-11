import {Deserializable} from "./Deserializable";

export default class Movimiento implements Deserializable {
  id: number;
  uuid: string;
  tipoOperacion: number;
  nombre: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
