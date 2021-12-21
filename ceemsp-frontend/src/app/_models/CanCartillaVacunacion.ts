import {Deserializable} from "./Deserializable";

export default class CanCartillaVacunacion implements Deserializable {

  expedidoPor: string;
  cedula: string;
  fechaExpedicion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
