import {Deserializable} from "./Deserializable";
import Usuario from "./Usuario";

export default class IncidenciaComentario implements Deserializable {

  comentario: string;
  usuario: Usuario;
  fecha: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
