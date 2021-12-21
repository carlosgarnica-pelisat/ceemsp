import {Deserializable} from "./Deserializable";
import Modalidad from "./Modalidad";
import Submodalidad from "./Submodalidad";

export default class EmpresaLicenciaColectiva implements Deserializable {
  id: number;
  uuid: string;
  numeroOficio: string;
  modalidad: Modalidad;
  submodalidad: Submodalidad;
  fechaInicio: string;
  fechaFin: string;
  rutaDocumento: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
