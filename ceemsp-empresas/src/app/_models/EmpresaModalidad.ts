import {Deserializable} from "./Deserializable";
import Modalidad from "./Modalidad";
import Submodalidad from "./Submodalidad";

export default class EmpresaModalidad implements Deserializable {
  uuid: string;
  modalidad: Modalidad;
  submodalidad: Submodalidad;
  fechaInicio: string;
  fechaFin: string;
  numeroRegistroFederal: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
