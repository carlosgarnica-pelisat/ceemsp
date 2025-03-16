import {Deserializable} from "./Deserializable";
import Empresa from "./Empresa";
import Incidencia from "./Incidencia";

export default class ValidarAcuse implements Deserializable {
  empresa: Empresa;
  numeroAcuse: string;
  incidencia: Incidencia;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
