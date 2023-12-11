import {Deserializable} from "./Deserializable";
import Uniforme from "./Uniforme";
import EmpresaUniformeElementoMovimiento from "./EmpresaUniformeElementoMovimiento";

export default class EmpresaUniformeElemento implements Deserializable {
  id: number;
  uuid: string;
  elemento: Uniforme;
  cantidad: number;
  movimientos: EmpresaUniformeElementoMovimiento[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
