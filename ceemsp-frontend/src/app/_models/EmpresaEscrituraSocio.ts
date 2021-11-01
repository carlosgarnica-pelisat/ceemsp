import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraSocio implements Deserializable {
  nombres: string;
  apellidos: string;
  sexo: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
