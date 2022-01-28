import {Deserializable} from "./Deserializable";
import VehiculoSubmarca from "./VehiculoSubmarca";

export default class VehiculoMarca implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  descripcion?: string;
  submarcas: VehiculoSubmarca[]

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
