import {Deserializable} from "./Deserializable";
import EmpresaDomicilio from "./EmpresaDomicilio";

export default class VehiculoDomicilio implements Deserializable {
  id: number;
  uuid: string;
  domicilioAnterior: EmpresaDomicilio;
  domicilioActual: EmpresaDomicilio;
  eliminado: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
