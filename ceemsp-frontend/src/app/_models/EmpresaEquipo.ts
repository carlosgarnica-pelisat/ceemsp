import {Deserializable} from "./Deserializable";
import Equipo from "./Equipo";

export default class EmpresaEquipo implements Deserializable {
  id: number;
  uuid: string;
  equipo: Equipo;
  cantidad: number;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
