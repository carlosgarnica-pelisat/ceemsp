import {Deserializable} from "./Deserializable";

export default class Cliente implements Deserializable {

  tipoPersona: string;
  rfc: string;
  nombreComercial: string;
  razonSocial: string;
  canes: boolean;
  armas: boolean;
  fechaInicio: string;
  fechaFin: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
