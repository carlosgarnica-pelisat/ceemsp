import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";

export default class PersonalVehiculo implements Deserializable {

  id: number;
  uuid: string;
  vehiculo: Vehiculo;
  observaciones: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
