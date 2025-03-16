import {Deserializable} from "./Deserializable";

export default class ProximaVisita implements Deserializable {
  tipoVisita: string;
  numeroSiguiente: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
