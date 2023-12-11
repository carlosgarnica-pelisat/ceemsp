import {Deserializable} from "./Deserializable";
import Equipo from "./Equipo";
import EmpresaEquipoMovimiento from "./EmpresaEquipoMovimiento";

export default class EmpresaEquipo implements Deserializable {
  id: number;
  uuid: string;
  equipo: Equipo;
  cantidad: number;
  movimientos: EmpresaEquipoMovimiento[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
