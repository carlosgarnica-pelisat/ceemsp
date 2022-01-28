import {Deserializable} from "./Deserializable";
import EmpresaUniformeElemento from "./EmpresaUniformeElemento";

export default class EmpresaUniforme implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  descripcion: string;
  elementos: EmpresaUniformeElemento[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
