import {Deserializable} from "./Deserializable";

export default class VehiculoFotografiaMetadata implements Deserializable {
  id: number;
  uuid: string;
  nombreArchivo: string;
  descripcion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
