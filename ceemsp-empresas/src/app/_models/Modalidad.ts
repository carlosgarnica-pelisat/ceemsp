import { Deserializable } from "./Deserializable";
import Submodalidad from "./Submodalidad";

export default class Modalidad implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;
  descripcion?: string;
  tipo: string;
  tieneSubmodalidades: boolean;
  submodalidades: Submodalidad[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
