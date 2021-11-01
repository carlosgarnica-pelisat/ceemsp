import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraApoderado implements Deserializable {
  nombres: string;
  apellidos: string;
  sexo: string;
  fechaInicio: string;
  fechaFin: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
