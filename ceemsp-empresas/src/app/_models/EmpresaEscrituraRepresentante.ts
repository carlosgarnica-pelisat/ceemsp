import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraRepresentante implements Deserializable {
  nombres: string;
  apellidos: string;
  sexo: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
