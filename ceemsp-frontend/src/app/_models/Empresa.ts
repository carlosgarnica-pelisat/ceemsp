import { Deserializable } from "./Deserializable";

export default class Empresa implements Deserializable {
  id: number;
  uuid: string;
  razonSocial: string;
  nombreComercial: string;
  tipoTramite: string;
  registro: string;
  tipoPersona: string;
  rfc: string;
  curp: string;
  sexo: string;
  correoElectronico: string;
  telefono: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
