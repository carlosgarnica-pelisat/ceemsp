import {Deserializable} from "./Deserializable";
import Uniforme from "./Uniforme";

export default class EmpresaUniformeElemento implements Deserializable {
  id: number;
  uuid: string;
  elemento: Uniforme;
  cantidad: number;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
