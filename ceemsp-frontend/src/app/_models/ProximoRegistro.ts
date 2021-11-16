import {Deserializable} from "./Deserializable";

export default class ProximoRegistro implements Deserializable {
  numeroSiguiente: string;
  tipo: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
