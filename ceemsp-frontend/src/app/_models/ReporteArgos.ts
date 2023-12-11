import {Deserializable} from "./Deserializable";

export default class ReporteArgos implements Deserializable {
  id: string;
  uuid: string;
  tipo: string;
  status: string;
  razon: string;
  fechaInicio: string;
  fechaFin: string;
  fechaCreacion: string;
  fechaActualizacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
