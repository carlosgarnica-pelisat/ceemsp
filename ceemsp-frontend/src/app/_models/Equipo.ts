import {Deserializable} from "./Deserializable";

export default class Equipo implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  descripcion?: string;
  formaEjecucion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
