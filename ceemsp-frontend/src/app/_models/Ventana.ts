import {Deserializable} from "./Deserializable";

export default class Ventana implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  fechaInicio: string;
  fechaFin: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
