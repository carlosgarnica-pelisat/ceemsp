import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";

export default class ExisteVehiculo implements Deserializable {

  existe: boolean;
  placas: string;
  numeroSerie: string;
  vehiculo: Vehiculo;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
