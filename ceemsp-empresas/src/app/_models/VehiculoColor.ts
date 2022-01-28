import {Deserializable} from "./Deserializable";

export default class VehiculoColor implements Deserializable {

  color: string;
  descripcion?: string;

  deserialize(input: any): this {
    return undefined;
  }
}
