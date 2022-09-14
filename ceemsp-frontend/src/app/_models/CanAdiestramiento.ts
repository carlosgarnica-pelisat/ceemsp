import {Deserializable} from "./Deserializable";
import TipoEntrenamiento from "./TipoEntrenamiento";

export default class CanAdiestramiento implements Deserializable {
  id: number;
  uuid: string;
  nombreInstructor: string;
  fechaConstancia: string;
  canTipoAdiestramiento: TipoEntrenamiento;
  eliminado: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
