import {Deserializable} from "./Deserializable";

export default class RealizarBusqueda implements Deserializable {
  palabraABuscar: string;
  filtro: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
