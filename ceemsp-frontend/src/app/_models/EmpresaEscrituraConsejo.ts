import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraConsejo implements Deserializable {
  id: number;
  uuid: string;
  nombres: string;
  apellidos: string;
  sexo: string;
  puesto: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
