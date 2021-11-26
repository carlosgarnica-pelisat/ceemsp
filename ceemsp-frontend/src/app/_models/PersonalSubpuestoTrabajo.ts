import {Deserializable} from "./Deserializable";

export default class PersonalSubpuestoTrabajo implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  descripcion: string;
  portacion: boolean;
  cuip: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
