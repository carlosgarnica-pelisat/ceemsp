import {Deserializable} from "./Deserializable";

export default class Calle implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
