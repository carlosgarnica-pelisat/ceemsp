import {Deserializable} from "./Deserializable";
import TipoEntrenamiento from "./TipoEntrenamiento";

export default class CanAdiestramiento implements Deserializable {
  nombreInstructor: string;
  fechaConstancia: string;
  canTipoAdiestramiento: TipoEntrenamiento;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
