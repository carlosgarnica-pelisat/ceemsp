import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraApoderado implements Deserializable {
  id: number;
  uuid: string;
  nombres: string;
  apellidos: string;
  apellidoMaterno: string;
  sexo: string;
  fechaInicio: string;
  fechaFin: string;
  curp: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
