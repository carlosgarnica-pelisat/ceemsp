import {Deserializable} from "./Deserializable";

export default class EmpresaEscrituraSocio implements Deserializable {
  id: number;
  uuid: string;
  nombres: string;
  apellidos: string;
  sexo: string;
  porcentajeAcciones: number;
  apellidoMaterno: string;
  curp: string;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
