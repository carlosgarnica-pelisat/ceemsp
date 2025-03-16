import {Deserializable} from "./Deserializable";

export default class NotificacionArgos implements Deserializable {
  id: number;
  uuid: string;
  motivo: string;
  descripcion: string;
  tipo: string;
  fechaCreacion: string;
  leido: boolean;
  ubicacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
