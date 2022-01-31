import {Deserializable} from "./Deserializable";

export default class IncidenciaComentario implements Deserializable {

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
