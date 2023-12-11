import {Deserializable} from "./Deserializable";
import EmpresaEscritura from "./EmpresaEscritura";
import Empresa from "./Empresa";

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
  eliminado: boolean;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;
  empresa: Empresa;
  escritura: EmpresaEscritura;
  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
