import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraConsejo implements Deserializable {
  nombres: string;
  apellidos: string;
  sexo: string;
  puesto: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
