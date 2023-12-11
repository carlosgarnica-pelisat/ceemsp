import {Deserializable} from "./Deserializable";

export default class EmpresaUniformeElementoMovimiento implements Deserializable {
  id: number;
  uuid: string;
  altas: number;
  bajas: number;
  cantidadActual: number;
  fechaCreacion: string;
  fechaActualizacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
