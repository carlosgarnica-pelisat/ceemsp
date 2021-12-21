import {Deserializable} from "./Deserializable";

export default class ComunicadoGeneral implements Deserializable {
  uuid: string;
  titulo: string;
  fechaPublicacion: string;
  descripcion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
