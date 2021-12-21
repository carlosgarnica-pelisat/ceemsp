import {Deserializable} from "./Deserializable";
import ClienteDomicilio from "./ClienteDomicilio";

export default class Cliente implements Deserializable {

  id: number;
  uuid: string;
  tipoPersona: string;
  rfc: string;
  nombreComercial: string;
  razonSocial: string;
  canes: boolean;
  armas: boolean;
  fechaInicio: string;
  fechaFin: string;

  domicilios: ClienteDomicilio[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
