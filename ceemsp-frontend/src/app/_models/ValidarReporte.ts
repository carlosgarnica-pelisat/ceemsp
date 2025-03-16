import {Deserializable} from "./Deserializable";
import Empresa from "./Empresa";
import Incidencia from "./Incidencia";

export default class ValidarReporte implements Deserializable {
  empresa: Empresa;
  mesano: string;
  fechaCreacion: string;
  numero: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
