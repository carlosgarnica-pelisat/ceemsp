import {Deserializable} from "./Deserializable";


export default class IncidenciaArchivoMetadata implements Deserializable {
  id: number;
  uuid: string;
  nombreArchivo: string;
  fechaCreacion: string;
  fechaActualizacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
