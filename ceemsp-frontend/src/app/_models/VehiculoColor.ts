import {Deserializable} from "./Deserializable";

export default class VehiculoColor implements Deserializable {

  id: number;
  uuid: string;
  color: string;
  descripcion?: string;

  deserialize(input: any): this {
    return undefined;
  }
}
