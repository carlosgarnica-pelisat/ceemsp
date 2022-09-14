import {Deserializable} from "./Deserializable";

export default class CanConstanciaSalud implements Deserializable {

  id: number;
  uuid: string;
  expedidoPor: string;
  cedula: string;
  fechaExpedicion: string;
  eliminado: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
