import {Deserializable} from "./Deserializable";
import EmpresaEscrituraSocio from "./EmpresaEscrituraSocio";
import EmpresaEscrituraApoderado from "./EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "./EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "./EmpresaEscrituraConsejo";

export default class EmpresaEscritura implements Deserializable {
  id: number;
  uuid: string;
  numeroEscritura: string;
  fechaEscritura: string;
  ciudad: string;
  tipoFedatario: string;
  numero: string;
  nombreFedatario: string;
  descripcion: string;
  socios: EmpresaEscrituraSocio[];
  apoderados: EmpresaEscrituraApoderado[];
  representantes: EmpresaEscrituraRepresentante[];
  consejos: EmpresaEscrituraConsejo[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
