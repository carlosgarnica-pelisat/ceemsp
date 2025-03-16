import { Deserializable } from "./Deserializable";
import Estado from "./Estado";
import Municipio from "./Municipio";
import Localidad from "./Localidad";
import Colonia from "./Colonia";
import Calle from "./Calle";

export default class EmpresaDomicilioTelefono implements Deserializable {
  id: number;
  uuid: string;
  telefono: string;
  tipoTelefono: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
