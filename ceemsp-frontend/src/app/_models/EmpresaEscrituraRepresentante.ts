import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraRepresentante implements Deserializable {
  id: number;
  uuid: string;
  nombres: string;
  apellidos: string;
  sexo: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
