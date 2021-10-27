import { Deserializable } from "./Deserializable";
import Modalidad from "./Modalidad";
import EmpresaModalidad from "./EmpresaModalidad";

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

  modalidades: EmpresaModalidad[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
