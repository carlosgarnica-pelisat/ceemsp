import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";
import Can from "./Can";

export default class PersonalCan implements Deserializable {

  id: number;
  uuid: string;
  can: Can;
  observaciones: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
