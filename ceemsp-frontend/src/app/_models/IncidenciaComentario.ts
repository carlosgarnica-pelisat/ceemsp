import {Deserializable} from "./Deserializable";

export default class IncidenciaComentario implements Deserializable {

  comentario: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
